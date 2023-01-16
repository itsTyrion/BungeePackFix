/*
 * "Commons Clause" License Condition v1.0
 *
 * The Software is provided to you by the Licensor under the License, as defined below, subject to the following condition.
 *
 * Without limiting other conditions in the License, the grant of rights under the License will not include, and the License does not grant to you,  right to Sell the Software.
 *
 * For purposes of the foregoing, "Sell" means practicing any or all of the rights granted to you under the License to provide to third parties, for a fee or other consideration (including without limitation fees for hosting or consulting/ support services related to the Software), a product or service whose value derives, entirely or substantially, from the functionality of the Software.  Any license notice or attribution required by the License must also include this Commons Cause License Condition notice.
 *
 * Software: BungeePackFix
 * License: Apache 2.0
 * Licensor: LoneDev
 */

package dev.lone.bungeepackfix.velocity;

import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import dev.lone.bungeepackfix.generic.PackUtility;
import dev.lone.bungeepackfix.generic.AbstractPlayerPackCache;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class VelocityPlayerPackCache extends AbstractPlayerPackCache
{
    public static HashMap<UUID, VelocityPlayerPackCache> playersCache = new HashMap<>();

    public static boolean isSamePack(Settings settings,
                              ServerConnection connection,
                              ResourcePackInfo playerPack,
                              ResourcePackInfo newPack,
                              boolean ignoreHashtagInUrl,
                              boolean checkHash,
                              boolean checkForced,
                              boolean checkMsg)
    {
        if (playerPack == newPack)
            return true;

        if (newPack == null)
            return false;

        if (connection.getServerInfo().getName().equals(settings.main_server_name))
        {
            String urlHashtag = PackUtility.getUrlHashtag(newPack.getUrl());
            VelocityPlayerPackCache playerCache = playersCache.get(connection.getPlayer().getUniqueId());
            if(playerCache != null)
            {
                // Check if the hashtag in main server URL changed
                if(!Objects.equals(playerCache.mainServerUrlHashtag, urlHashtag))
                {
                    playerCache.mainServerUrlHashtag = urlHashtag;
                    return false;
                }
            }
            else
            {
                playerCache = new VelocityPlayerPackCache();
                playersCache.put(connection.getPlayer().getUniqueId(), playerCache);
            }

            playerCache.mainServerUrlHashtag = urlHashtag;
        }

        final String newUrl = PackUtility.removeHashtag(ignoreHashtagInUrl, newPack.getUrl());
        final String prevUrl = PackUtility.removeHashtag(ignoreHashtagInUrl, playerPack.getUrl());

        return (Objects.equals(prevUrl, newUrl)) &&
                (!checkHash || Objects.equals(playerPack.getHash(), newPack.getHash())) &&
                (!checkForced || Objects.equals(playerPack.getShouldForce(), newPack.getShouldForce())) &&
                (!checkMsg || Objects.equals(playerPack.getPrompt(), newPack.getPrompt()))
                ;
    }
}