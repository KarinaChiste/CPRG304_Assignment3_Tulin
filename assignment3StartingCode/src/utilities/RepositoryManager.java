package utilities;

import java.io.*;
import implementations.BSTree;

/**
 * Handles serialization of BSTree to/from repository.ser
 * 
 * Usage:
 *   BSTree<E> tree = RepositoryManager.loadRepository();
 *   if (tree == null) tree = new BSTree<>();
 *   // ... process files and add to tree ...
 *   RepositoryManager.saveRepository(tree);
 */
public class RepositoryManager {
    
    private static final String REPOSITORY_FILE = "repository.ser";
    
    /**
     * Saves the BSTree to repository.ser using Java serialization
     * 
     * @param tree The BSTree to save
     * @throws IOException if unable to write to file
     */
    public static <E extends Comparable<? super E>> void saveRepository(BSTree<E> tree) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(REPOSITORY_FILE))) {
            out.writeObject(tree);
        }
    }
    
    /**
     * Loads the BSTree from repository.ser if it exists
     * 
     * @return The loaded BSTree, or null if file doesn't exist
     */
    @SuppressWarnings("unchecked")
    public static <E extends Comparable<? super E>> BSTree<E> loadRepository() {
        File file = new File(REPOSITORY_FILE);
        
        if (!file.exists()) {
            return null;
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(REPOSITORY_FILE))) {
            return (BSTree<E>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading repository: " + e.getMessage());
            return null;
        }
    }
}
