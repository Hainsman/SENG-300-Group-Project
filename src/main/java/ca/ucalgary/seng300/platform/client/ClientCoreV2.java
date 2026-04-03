package ca.ucalgary.seng300.platform.client;

import ca.ucalgary.seng300.ui.CentralController;
/**
 * Compatibility facade so merged UI code can call {@code ClientCoreV2.getInstance()}.
 * Networking behavior is inherited directly from {@link ClientCore}.
 */
public final class ClientCoreV2 extends ClientCore {
    private static ClientCoreV2 instance;

    private ClientCoreV2(CentralController root) {
        super(root);
    }

    public static synchronized ClientCoreV2 getInstance() {
        if (instance == null) {
            CentralController root = CentralController.getInstance();
            if (root == null) {
                throw new IllegalStateException("CentralController must initialize before ClientCoreV2.getInstance()");
            }
            instance = new ClientCoreV2(root);
        }
        return instance;
    }
}
