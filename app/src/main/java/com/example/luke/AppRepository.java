package com.example.luke;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class AppRepository {
    private static final String PREF_NAME = "AppState";
    private static final String KEY_HELPER_CITY = "helper_city";
    private static final String KEY_ACTIVE_POINT_ADDRESS = "active_point_address";

    private static AppRepository INSTANCE;

    private final AppDatabaseHelper databaseHelper;
    private final SharedPreferences preferences;

    private AppRepository(Context context) {
        databaseHelper = new AppDatabaseHelper(context);
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        seedIfNeeded();
    }

    public static synchronized void init(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppRepository(context.getApplicationContext());
        }
    }

    public static synchronized AppRepository getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("AppRepository is not initialized");
        }
        return INSTANCE;
    }

    public String getHelperCity() {
        return preferences.getString(KEY_HELPER_CITY, "Красноярск");
    }

    public void setHelperCity(String helperCity) {
        if (helperCity != null && !helperCity.trim().isEmpty()) {
            preferences.edit().putString(KEY_HELPER_CITY, helperCity.trim()).apply();
        }
    }

    public List<NeedPoint> getNeedPointsForCity(String city) {
        List<PointProfile> points = databaseHelper.getPointsByCity(city);
        List<NeedPoint> result = new ArrayList<>();
        for (PointProfile point : points) {
            result.add(toNeedPoint(point));
        }
        return result;
    }

    public List<PointProfile> getPointProfilesForCity(String city) {
        return databaseHelper.getPointsByCity(city);
    }

    public List<PointProfile> getAllPointProfiles() {
        return databaseHelper.getAllPoints();
    }

    public PointProfile getPointByAddress(String address) {
        return databaseHelper.getPointByAddress(address);
    }

    public PointProfile getPointByName(String pointName) {
        return databaseHelper.getPointByName(pointName);
    }

    public PointProfile getActivePoint() {
        String activeAddress = preferences.getString(KEY_ACTIVE_POINT_ADDRESS, null);
        PointProfile point = databaseHelper.getPointByAddress(activeAddress);
        if (point != null) return point;

        List<PointProfile> allPoints = databaseHelper.getAllPoints();
        if (!allPoints.isEmpty()) {
            setActivePointAddress(allPoints.get(0).getAddress());
            return allPoints.get(0);
        }
        return null;
    }

    public void setActivePointAddress(String address) {
        if (address != null && !address.trim().isEmpty()) {
            preferences.edit().putString(KEY_ACTIVE_POINT_ADDRESS, address.trim()).apply();
        }
    }

    public boolean activatePointByName(String pointName) {
        PointProfile point = getPointByName(pointName);
        if (point == null) return false;
        setActivePointAddress(point.getAddress());
        return true;
    }

    public void upsertPoint(PointProfile profile) {
        ensureCoordinates(profile);
        databaseHelper.upsertPoint(profile);
        setActivePointAddress(profile.getAddress());
    }

    public boolean updatePoint(String originalAddress, PointProfile profile) {
        ensureCoordinates(profile);
        boolean updated = databaseHelper.updatePoint(originalAddress, profile);
        if (updated) {
            setActivePointAddress(profile.getAddress());
        }
        return updated;
    }

    public List<Product> getActiveProducts() {
        PointProfile point = getActivePoint();
        if (point == null) return new ArrayList<>();
        return databaseHelper.getProductsForPoint(point.getAddress());
    }

    public Product getActiveProduct(int index) {
        List<Product> products = getActiveProducts();
        if (index < 0 || index >= products.size()) return null;
        return products.get(index);
    }

    public Product getProductById(long id) {
        return databaseHelper.getProductById(id);
    }

    public void addProductToActivePoint(Product product) {
        PointProfile point = getActivePoint();
        if (point != null) {
            databaseHelper.insertProduct(point.getAddress(), product);
        }
    }

    public boolean updateActiveProduct(Product updated) {
        return databaseHelper.updateProduct(updated);
    }

    public boolean updateActiveProduct(int index, Product updated) {
        Product current = getActiveProduct(index);
        if (current == null) return false;
        updated.setId(current.getId());
        updated.setPointAddress(current.getPointAddress());
        return updateActiveProduct(updated);
    }

    public boolean deleteProduct(long id) {
        return databaseHelper.deleteProduct(id);
    }

    public NeedPoint toNeedPoint(PointProfile point) {
        String[] items;
        List<Product> products = databaseHelper.getProductsForPoint(point.getAddress());
        if (products.isEmpty()) {
            items = new String[]{"Потребности появятся после публикации"};
        } else {
            items = new String[products.size()];
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                items[i] = product.getName() + ": " + product.getTotalQuantity() + " шт (собрано " + product.getCollectedQuantity() + ")";
            }
        }

        String urgency = "ОБЫЧНО";
        for (Product product : products) {
            if ("Критическая".equals(product.getUrgency()) || "СРОЧНО".equals(product.getUrgency())) {
                urgency = "СРОЧНО";
                break;
            }
        }

        String need = products.isEmpty()
                ? "Нет активных потребностей"
                : "Нужно позиций: " + products.size();

        return new NeedPoint(
                point.getPointName(),
                point.getCity(),
                point.getAddress(),
                point.getWorkHours(),
                items,
                point.getContactName(),
                point.getContactPhone(),
                need,
                urgency,
                R.drawable.ic_need_point
        );
    }

    private void seedIfNeeded() {
        if (!databaseHelper.isPointTableEmpty()) {
            return;
        }

        PointProfile krasnoyarsk = new PointProfile(
                "Штаб Свои",
                "Красноярск",
                "Ленинский пр-т, 30",
                GeoUtils.coordinatesForPoint("Красноярск", "Ленинский пр-т, 30")[0],
                GeoUtils.coordinatesForPoint("Красноярск", "Ленинский пр-т, 30")[1],
                "Анна",
                "+7 (999) 999-99-99",
                "anna@example.ru",
                "1111",
                "10:00–20:00"
        );
        databaseHelper.upsertPoint(krasnoyarsk);
        databaseHelper.insertProduct(krasnoyarsk.getAddress(), new Product("Зубная паста", "Мятная, отбеливающая", 50, 5, "Обычная"));
        databaseHelper.insertProduct(krasnoyarsk.getAddress(), new Product("Носки тёплые", "Зимние", 20, 3, "Критическая"));

        PointProfile moscow = new PointProfile(
                "Пункт Сбор №1",
                "Москва",
                "Дмитровское ш., 23",
                GeoUtils.coordinatesForPoint("Москва", "Дмитровское ш., 23")[0],
                GeoUtils.coordinatesForPoint("Москва", "Дмитровское ш., 23")[1],
                "Андрей",
                "+7 (000) 000-00-00",
                "andrey@example.ru",
                "2222",
                "10:00–22:00"
        );
        databaseHelper.upsertPoint(moscow);
        databaseHelper.insertProduct(moscow.getAddress(), new Product("Термобельё", "Размер M", 15, 2, "Обычная"));
        databaseHelper.insertProduct(moscow.getAddress(), new Product("Свечи", "Оконные", 30, 10, "Высокая"));

        setHelperCity("Красноярск");
        setActivePointAddress(krasnoyarsk.getAddress());
    }

    private void ensureCoordinates(PointProfile profile) {
        if (profile.getLatitude() == 0 && profile.getLongitude() == 0) {
            double[] coords = GeoUtils.coordinatesForPoint(profile.getCity(), profile.getAddress());
            profile.setLatitude(coords[0]);
            profile.setLongitude(coords[1]);
        }
    }
}
