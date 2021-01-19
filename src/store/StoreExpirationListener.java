package store;

public interface StoreExpirationListener {
    void onExpired(Store s);
}
