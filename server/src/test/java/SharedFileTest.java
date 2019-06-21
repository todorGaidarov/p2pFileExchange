import org.junit.Test;
import project.p2p.exchange.torrent.server.impl.SharedFile;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SharedFileTest {

    @Test
    public void testAddingSeederFileNoPreviousSeeders() {
        Map<String, Object> sharedFileInfo = new HashMap<>();
        sharedFileInfo.put(SharedFile.NAME, "torrent");
        sharedFileInfo.put(SharedFile.SIZE_BYTES, 1234567L);
        sharedFileInfo.put(SharedFile.RELATIVE_PATH, "./torrent");

        SharedFile sharedFile = new SharedFile(sharedFileInfo, null);
        int numberSeeders = sharedFile.getNumberSeeders();
        assertEquals("Number of seeders must be 0 for shared file no seeders.", 0, numberSeeders);

        sharedFile.addSeeder(new InetSocketAddress("host1", 1234));

        Set<InetSocketAddress> seeders = sharedFile.getSeeders();
        int numberOfSeeders = sharedFile.getNumberSeeders();

        assertEquals("numberOfSeeders must match the size of seeders.", seeders.size(), numberOfSeeders);
        assertEquals("Given no previous seeders, numberOfSeeders must be 1 after adding.", 1, numberOfSeeders);

        InetSocketAddress expectedSeeder = new InetSocketAddress("host1", 1234);
        assertTrue("Seeders must contain the added seeder.", seeders.contains(expectedSeeder));
    }

    @Test
    public void testRemoveSeederFileWithSeeders() {
        Map<String, Object> sharedFileInfo = new HashMap<>();
        sharedFileInfo.put(SharedFile.NAME, "torrent");
        sharedFileInfo.put(SharedFile.SIZE_BYTES, 1234567L);
        sharedFileInfo.put(SharedFile.RELATIVE_PATH, "./torrent");

        InetSocketAddress seeder = new InetSocketAddress("host", 12345);
        Set<InetSocketAddress> seeders = new HashSet<>();
        seeders.add(seeder);

        SharedFile sharedFile = new SharedFile(sharedFileInfo, seeders);
        Set<InetSocketAddress> actualSeeders = sharedFile.getSeeders();
        int numberOfSeeders = sharedFile.getNumberSeeders();
        assertEquals("numberOfSeeders of the shared file must be 1.", 1, numberOfSeeders);
        assertEquals("Size of seeders and numberOfSeeders must match.", seeders.size(), numberOfSeeders);

        InetSocketAddress expectedSeeder = new InetSocketAddress("host", 12345);
        assertTrue("Seeders must contain the initial seeder.", seeders.contains(expectedSeeder));

        sharedFile.removeSeeder(expectedSeeder);
        actualSeeders = sharedFile.getSeeders();
        assertTrue("There must be no seeders after removal of the only seeder", actualSeeders.size() == 0);
        numberOfSeeders = sharedFile.getNumberSeeders();
        assertEquals("numberOfSeeders must be 0 after removal of the only seeder", 0, numberOfSeeders);
    }
}
