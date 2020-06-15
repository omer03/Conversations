package eu.siacs.conversations.tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import eu.siacs.conversations.entities.Account;
import eu.siacs.conversations.entities.Contact;
import eu.siacs.conversations.entities.Conversation;
import eu.siacs.conversations.entities.DownloadableFile;
import eu.siacs.conversations.entities.Message;
import eu.siacs.conversations.entities.Presence;
import eu.siacs.conversations.ui.RecordingActivity;
import rocks.xmpp.addr.Jid;

import static eu.siacs.conversations.entities.Conversational.MODE_SINGLE;
import static eu.siacs.conversations.entities.Message.TYPE_IMAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class TestingClass {

    Jid jid = new Jid() {

        @Override
        public int compareTo(Jid o) {
            return 0;
        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public char charAt(int index) {
            return 0;
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return null;
        }

        @Override
        public IntStream chars() {
            return null;
        }

        @Override
        public IntStream codePoints() {
            return null;
        }

        @Override
        public boolean isFullJid() {
            return false;
        }

        @Override
        public boolean isBareJid() {
            return false;
        }

        @Override
        public boolean isDomainJid() {
            return false;
        }

        @Override
        public Jid asBareJid() {
            return null;
        }

        @Override
        public Jid withLocal(CharSequence local) {
            return null;
        }

        @Override
        public Jid withResource(CharSequence resource) {
            return null;
        }

        @Override
        public Jid atSubdomain(CharSequence subdomain) {
            return null;
        }

        @Override
        public String getLocal() {
            return null;
        }

        @Override
        public String getEscapedLocal() {
            return null;
        }

        @Override
        public String getDomain() {
            return null;
        }

        @Override
        public String getResource() {
            return null;
        }

        @Override
        public String toEscapedString() {
            return null;
        }
    };

    private Account testAccount;
    private File testFile;
    private String photoUri;
    private String imagePath;
    private int lastSeen;
    private int invalidLastSeen;
    @Before
    public void CreateAccount() throws IOException {
        testAccount = new Account(jid,"hva");
        testFile  = tempFolder.newFile("testFile.txt");
        photoUri = "local/test";
        imagePath = "local/test";
        lastSeen = 1000;
        invalidLastSeen = -500;
    }

    @Test(timeout = 1000)
    public void testSendFile() {
        String path = testFile.getAbsolutePath();
        assertNotNull(testFile);
        DownloadableFile downloadFile = new DownloadableFile(path);
        assertEquals(downloadFile.getAbsolutePath(),path);
        assertEquals(downloadFile.length(),testFile.length());
        assertEquals(testFile, downloadFile.getAbsoluteFile());
    }

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(timeout = 1000)
    public void testSavingFile()  {
        String path = testFile.getAbsolutePath();
        assertNotNull(testFile);
        DownloadableFile downloadFile = new DownloadableFile(path);
        assertNotNull(downloadFile);
    }

    @Test(timeout = 250)
    public void testViewImage()
    {
        Conversation testConversation = new Conversation("testAccount", testAccount,jid,MODE_SINGLE);
        Message testImgMessage = new Message(testConversation, "Here is an example image.",0);

        testImgMessage.setRelativeFilePath(imagePath);
        testImgMessage.setType(TYPE_IMAGE);

        assertEquals(testImgMessage.getType(), TYPE_IMAGE);
        assertTrue(testImgMessage.isFileOrImage());
        assertEquals(testImgMessage.getRelativeFilePath(),imagePath);
    }

    @Test(timeout = 1000)
    public void testRecordVoiceMessage()
    {
        RecordingActivity mockRecording =  Mockito.mock(RecordingActivity.class);
        mockRecording.startRecording();
        mockRecording.stopRecording(true);

        verify(mockRecording).startRecording();
        verify(mockRecording).stopRecording(true);
    }

    @Test
    public void testAddNewContact()
    {
        Contact mockContact = mock(Contact.class);
        Contact testContact = new Contact(jid);

        testContact.setAccount(testAccount);
        mockContact.setAccount(testAccount);
        verify(mockContact).setAccount(testAccount);
        //assertNull(testContact.getAccount());

        testContact.setPhotoUri(photoUri);
        when(mockContact.getProfilePhoto()).thenReturn(photoUri);
        assertEquals(mockContact.getProfilePhoto(),testContact.getProfilePhoto());
    }

    @Test(timeout = 250)
    public void testLastSeenTime()
    {
        Contact mockContact = Mockito.mock(Contact.class);
        Contact testContact = new Contact(jid);

        testContact.setLastseen(invalidLastSeen);
        assertFalse("Last seen can't be a negative timestamp",testContact.getLastseen() < 0);

        testContact.setLastseen(lastSeen);
        mockContact.setLastseen(lastSeen);

        verify(mockContact).setLastseen(lastSeen);
        assertEquals(mockContact.getLastseen(),testContact.getLastseen());
    }

    @Test(timeout = 250)
    public void testChangeStatus()
    {
        Contact testContact = new Contact(jid);
        Presence onlinePresence = new Presence(Presence.Status.ONLINE,"testPresence","testPresence","testPresence","testPresence");
        Presence doNotDisturbPresence = new Presence(Presence.Status.DND,"testPresence","testPresence","testPresence","testPresence");
        Presence awayPresence = new Presence(Presence.Status.AWAY,"testPresence","testPresence","testPresence","testPresence");

        testContact.updatePresence("testPresence",onlinePresence);
        assertEquals(testContact.getShownStatus(),Presence.Status.ONLINE);

        testContact.updatePresence("testPresence",doNotDisturbPresence);
        assertEquals(testContact.getShownStatus(),Presence.Status.DND);

        testContact.updatePresence("testPresence",awayPresence);
        assertEquals(testContact.getShownStatus(),Presence.Status.AWAY);
    }

    @Test(timeout = 250)
    public void testSendMessage(){
        String messageBody = "test message";
        Conversation testConversation = new Conversation("testAccount", testAccount,jid,MODE_SINGLE);
        Message testMessage = new Message(testConversation, messageBody ,0);
        assertNotNull(testMessage);
        assertEquals("Message bodies should be equal",testMessage.getBody(),messageBody);
        assertFalse("Only files and images needs uploading.", testMessage.needsUploading());
    }
}
