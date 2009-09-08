package org.cometd.javascript.jquery.extension;

import java.net.URL;

import org.cometd.javascript.jquery.AbstractCometdJQueryTest;
import org.testng.annotations.Test;

/**
 * @version $Revision$ $Date$
 */
public class CometdReloadExtensionTest extends AbstractCometdJQueryTest
{
    @Test
    public void testReload() throws Exception
    {
        URL cookiePlugin = new URL(contextURL + "/jquery/jquery.cookie.js");
        evaluateURL(cookiePlugin);
        URL reloadExtension = new URL(contextURL + "/org/cometd/ReloadExtension.js");
        evaluateURL(reloadExtension);
        URL jqueryReloadExtension = new URL(contextURL + "/jquery/jquery.cometd-reload.js");
        evaluateURL(jqueryReloadExtension);

        evaluateScript("$.cometd.configure({url: '" + cometURL + "', logLevel: 'debug'});");
        evaluateScript("$.cometd.handshake();");
        Thread.sleep(500); // Wait for the long poll

        // Get the clientId
        String clientId = evaluateScript("$.cometd.getClientId();");

        // Calling reload() results in the cookie being written
        evaluateScript("$.cometd.reload();");

        // Reload the page
        destroyPage();
        initPage();
        evaluateURL(cookiePlugin);
        evaluateURL(reloadExtension);
        evaluateURL(jqueryReloadExtension);

        evaluateScript("$.cometd.configure({url: '" + cometURL + "', logLevel: 'debug'});");
        evaluateScript("$.cometd.handshake();");
        Thread.sleep(500); // Wait for the long poll

        String newClientId = evaluateScript("$.cometd.getClientId();");
        assert clientId.equals(newClientId);

        evaluateScript("$.cometd.disconnect();");
        Thread.sleep(500); // Wait for the disconnect to return

        // Be sure the cookie has been removed on disconnect
        String cookie = evaluateScript("org.cometd.COOKIE.get('org.cometd.reload');");
        assert cookie == null;
    }

    @Test
    public void testReloadWithTimestamp() throws Exception
    {
        evaluateScript("$.cometd.setLogLevel('debug');");
        // Add timestamp extension before the reload extension
        URL timestampExtension = new URL(contextURL + "/org/cometd/TimeStampExtension.js");
        evaluateURL(timestampExtension);
        URL jqueryTimestampExtension = new URL(contextURL + "/jquery/jquery.cometd-timestamp.js");
        evaluateURL(jqueryTimestampExtension);

        URL cookiePlugin = new URL(contextURL + "/jquery/jquery.cookie.js");
        evaluateURL(cookiePlugin);
        URL reloadExtension = new URL(contextURL + "/org/cometd/ReloadExtension.js");
        evaluateURL(reloadExtension);
        URL jqueryReloadExtension = new URL(contextURL + "/jquery/jquery.cometd-reload.js");
        evaluateURL(jqueryReloadExtension);

        evaluateScript("$.cometd.configure({url: '" + cometURL + "', logLevel: 'debug'});");
        evaluateScript("$.cometd.handshake();");
        Thread.sleep(500); // Wait for the long poll

        // Get the clientId
        String clientId = evaluateScript("$.cometd.getClientId();");

        // Calling reload() results in the cookie being written
        evaluateScript("$.cometd.reload();");

        // Reload the page
        destroyPage();
        initPage();

        evaluateScript("$.cometd.setLogLevel('debug');");
        evaluateURL(timestampExtension);
        evaluateURL(jqueryTimestampExtension);
        evaluateURL(cookiePlugin);
        evaluateURL(reloadExtension);
        evaluateURL(jqueryReloadExtension);

        evaluateScript("$.cometd.configure({url: '" + cometURL + "', logLevel: 'debug'});");
        evaluateScript("$.cometd.handshake();");
        Thread.sleep(500); // Wait for the long poll

        String newClientId = evaluateScript("$.cometd.getClientId();");
        assert clientId.equals(newClientId);

        evaluateScript("$.cometd.disconnect();");
        Thread.sleep(500); // Wait for the disconnect to return
    }
}