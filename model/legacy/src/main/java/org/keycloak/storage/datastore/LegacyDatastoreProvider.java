package org.keycloak.storage.datastore;

import org.keycloak.models.ClientProvider;
import org.keycloak.models.ClientScopeProvider;
import org.keycloak.models.GroupProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RoleProvider;
import org.keycloak.models.cache.CacheRealmProvider;
import org.keycloak.storage.ClientScopeStorageManager;
import org.keycloak.storage.ClientStorageManager;
import org.keycloak.storage.DatastoreProvider;
import org.keycloak.storage.GroupStorageManager;
import org.keycloak.storage.RoleStorageManager;

public class LegacyDatastoreProvider implements DatastoreProvider {

    private final LegacyDatastoreProviderFactory factory;
    private final KeycloakSession session;

    private ClientProvider clientProvider;
    private ClientScopeProvider clientScopeProvider;
    private GroupProvider groupProvider;
    private RealmProvider realmProvider;
    private RoleProvider roleProvider;

    private ClientScopeStorageManager clientScopeStorageManager;
    private RoleStorageManager roleStorageManager;
    private GroupStorageManager groupStorageManager;
    private ClientStorageManager clientStorageManager;

    public LegacyDatastoreProvider(LegacyDatastoreProviderFactory factory, KeycloakSession session) {
        this.factory = factory;
        this.session = session;
    }

    @Override
    public void close() {
    }

    public ClientProvider clientStorageManager() {
        if (clientStorageManager == null) {
            clientStorageManager = new ClientStorageManager(session, factory.getClientStorageProviderTimeout());
        }
        return clientStorageManager;
    }

    public ClientScopeProvider clientScopeStorageManager() {
        if (clientScopeStorageManager == null) {
            clientScopeStorageManager = new ClientScopeStorageManager(session);
        }
        return clientScopeStorageManager;
    }

    public RoleProvider roleStorageManager() {
        if (roleStorageManager == null) {
            roleStorageManager = new RoleStorageManager(session, factory.getRoleStorageProviderTimeout());
        }
        return roleStorageManager;
    }

    public GroupProvider groupStorageManager() {
        if (groupStorageManager == null) {
            groupStorageManager = new GroupStorageManager(session);
        }
        return groupStorageManager;
    }

    private ClientProvider getClientProvider() {
        // TODO: Extract ClientProvider from CacheRealmProvider and use that instead
        ClientProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return clientStorageManager();
        }
    }

    private ClientScopeProvider getClientScopeProvider() {
        // TODO: Extract ClientScopeProvider from CacheRealmProvider and use that instead
        ClientScopeProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return clientScopeStorageManager();
        }
    }

    private GroupProvider getGroupProvider() {
        // TODO: Extract GroupProvider from CacheRealmProvider and use that instead
        GroupProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return groupStorageManager();
        }
    }

    private RealmProvider getRealmProvider() {
        CacheRealmProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return session.getProvider(RealmProvider.class);
        }
    }

    private RoleProvider getRoleProvider() {
        // TODO: Extract RoleProvider from CacheRealmProvider and use that instead
        RoleProvider cache = session.getProvider(CacheRealmProvider.class);
        if (cache != null) {
            return cache;
        } else {
            return roleStorageManager();
        }
    }

    @Override
    public ClientProvider clients() {
        if (clientProvider == null) {
            clientProvider = getClientProvider();
        }
        return clientProvider;
    }

    @Override
    public ClientScopeProvider clientScopes() {
        if (clientScopeProvider == null) {
            clientScopeProvider = getClientScopeProvider();
        }
        return clientScopeProvider;
    }

    @Override
    public GroupProvider groups() {
        if (groupProvider == null) {
            groupProvider = getGroupProvider();
        }
        return groupProvider;
    }

    @Override
    public RealmProvider realms() {
        if (realmProvider == null) {
            realmProvider = getRealmProvider();
        }
        return realmProvider;
    }

    @Override
    public RoleProvider roles() {
        if (roleProvider == null) {
            roleProvider = getRoleProvider();
        }
        return roleProvider;
    }
}