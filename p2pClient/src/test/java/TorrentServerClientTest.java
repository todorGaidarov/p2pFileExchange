import net.bytebuddy.asm.Advice;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import project.p2p.exchange.torrent.p2pClient.P2PClient;
import project.p2p.exchange.torrent.p2pClient.SharedFile;
import project.p2p.exchange.torrent.p2pClient.TorrentServerClient;
import project.p2p.exchange.torrent.p2pClient.impl.SocketClient;
import project.p2p.exchange.torrent.p2pClient.impl.TorrentServerClientImpl;

import java.net.InetSocketAddress;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class TorrentServerClientTest {

    private static final String RESPONSE_LIST_TORRENTS_SUCCESS = TorrentServerClient.RESPONSE_PREF_SUCCEED + " torrent1, torrent2, torrent3";
    private static final String RESPONSE_GET_TORRENT_INFO_SUCCESS =
            TorrentServerClient.RESPONSE_PREF_SUCCEED + ' ' + SharedFile.NAME + ":testName.file;" +
                    SharedFile.SIZE_BYTES + ":12345678;" +
                    SharedFile.RELATIVE_PATH + ":./testName.file;" +
                    SharedFile.SEEDERS + ":host1:1234,127.0.0.1:6545;" +
                    SharedFile.NUMBER_SEEDERS + ":2;";

    private SocketClient socketClient;


    @Before
    public void setupBeforeEachTest() throws Exception {
        socketClient = Mockito.mock(SocketClient.class);

    }

    @Test
    public void testListTorrentsSuccessful() throws Exception {
        String listRequest = "<:|:>" + TorrentServerClient.REQUESST_PREF_LIST_TORRENTS + "<:|:>";
        Mockito.when(socketClient.sendRequest(Mockito.eq(listRequest))).
                thenReturn(RESPONSE_LIST_TORRENTS_SUCCESS);

        TorrentServerClientImpl torrentServerClient = new TorrentServerClientImpl(socketClient, 1212);
        List<String> expectedTorrents = Arrays.asList("torrent1", "torrent2", "torrent3");
        List<String> actualTorrents = torrentServerClient.listTorrents();

        assertEquals("Actual list of torrents does not match the expected", expectedTorrents, actualTorrents);
    }

    @Test
    public void testGetTorrentInfoSuccessful() throws Exception {
        String request = "<:|:>" + TorrentServerClient.REQUESST_PREF_GET_TORRENT_INFO + " torrentName<:|:>";
        Mockito.when(socketClient.sendRequest(Mockito.eq(request))).thenReturn(RESPONSE_GET_TORRENT_INFO_SUCCESS);

        TorrentServerClient torrentServerClient = new TorrentServerClientImpl(socketClient, 1212);
        Map<String, Object> expectedInfoAttr = new HashMap<>();
        expectedInfoAttr.put(SharedFile.NAME,  "testName.file");
        expectedInfoAttr.put(SharedFile.SIZE_BYTES, 12345678L);
        expectedInfoAttr.put(SharedFile.RELATIVE_PATH, "./testName.file");

        Set<InetSocketAddress> seeders = new HashSet<>(Arrays.asList(new InetSocketAddress("host1", 1234), new InetSocketAddress("127.0.0.1", 6545)));
        SharedFile expectedInfo = new SharedFile(expectedInfoAttr, seeders);
        SharedFile actualInfo = torrentServerClient.getTorrentInfo("torrentName");
        System.out.println("Expected: " + expectedInfo);
        System.out.println("Actual: " + actualInfo);
        assertEquals("Mismatch between expected and actual shared file.", expectedInfo, actualInfo);
    }

    @Test
    public void testRegisterTorrentSuccessful() throws Exception {
        Map<String, Object> sharedFileAttr = new HashMap<>();
        sharedFileAttr.put(SharedFile.NAME, "torrentName");
        sharedFileAttr.put(SharedFile.SIZE_BYTES, 123456L);
        sharedFileAttr.put(SharedFile.RELATIVE_PATH, "./torrentName");
        SharedFile sharedFile = new SharedFile(sharedFileAttr, null);
        String request = "<:|:>" + TorrentServerClient.REQUESST_PREF_REGISTER_TORRENT + ' ' + sharedFile.toString() + P2PClient.PROP_PEER_PORT + ":1212;<:|:>";

        Mockito.when(socketClient.sendRequest(Mockito.eq(request))).thenReturn(TorrentServerClient.RESPONSE_PREF_SUCCEED);
        TorrentServerClient torrentServerClient = new TorrentServerClientImpl(socketClient, 1212);
        torrentServerClient.registerTorrent(sharedFile);
        ArgumentCaptor<String> requestCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(socketClient, Mockito.times(1)).sendRequest(requestCaptor.capture());
        String expectedRequest = "<:|:>register {" + SharedFile.NAME + ":torrentName;" +
                SharedFile.SIZE_BYTES + ":123456;" +
                SharedFile.RELATIVE_PATH + ":./torrentName;}"+ P2PClient.PROP_PEER_PORT + ":1212;<:|:>";

        String actualRequest = requestCaptor.getValue();
        assertEquals("Mismatch between expected and actual request for registering torrent.", expectedRequest, actualRequest);
    }
}
