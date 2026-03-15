package com.lirxowo.taczcreativesupplement.client.data;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.client.resource.ClientAssetsManager;
import com.tacz.guns.client.resource.pojo.PackInfo;
import com.tacz.guns.resource.index.CommonAttachmentIndex;
import com.tacz.guns.util.AllowAttachmentTagMatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class AttachmentOrganizer {

    public record PackGroup(String namespace, Component displayName, List<ResourceLocation> attachmentIds) {
    }

    public static List<PackGroup> organize(ResourceLocation gunId, AttachmentType type) {
        Map<String, List<Map.Entry<ResourceLocation, CommonAttachmentIndex>>> grouped = new LinkedHashMap<>();

        Set<Map.Entry<ResourceLocation, CommonAttachmentIndex>> allAttachments = TimelessAPI.getAllCommonAttachmentIndex();
        for (Map.Entry<ResourceLocation, CommonAttachmentIndex> entry : allAttachments) {
            CommonAttachmentIndex index = entry.getValue();
            if (index.getType() != type) continue;
            if (index.getPojo().isHidden()) continue;
            ResourceLocation attachmentId = entry.getKey();
            if (!AllowAttachmentTagMatcher.match(gunId, attachmentId)) continue;

            String ns = attachmentId.getNamespace();
            grouped.computeIfAbsent(ns, k -> new ArrayList<>()).add(entry);
        }

        List<PackGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<Map.Entry<ResourceLocation, CommonAttachmentIndex>>> nsEntry : grouped.entrySet()) {
            String ns = nsEntry.getKey();
            List<Map.Entry<ResourceLocation, CommonAttachmentIndex>> entries = nsEntry.getValue();

            entries.sort(Comparator.<Map.Entry<ResourceLocation, CommonAttachmentIndex>>comparingInt(e -> e.getValue().getSort()).thenComparing(e -> e.getKey().toString()));

            List<ResourceLocation> sortedIds = entries.stream().map(Map.Entry::getKey).collect(Collectors.toList());

            Component displayName = resolveDisplayName(ns);
            groups.add(new PackGroup(ns, displayName, sortedIds));
        }

        groups.sort((a, b) -> {
            if ("tacz".equals(a.namespace())) return -1;
            if ("tacz".equals(b.namespace())) return 1;
            return a.displayName().getString().compareToIgnoreCase(b.displayName().getString());
        });

        return groups;
    }

    private static Component resolveDisplayName(String namespace) {
        PackInfo info = ClientAssetsManager.INSTANCE.getPackInfo(namespace);
        if (info != null) {
            String name = info.getName();
            if (name != null && !name.isEmpty()) {
                return Component.translatable(name);
            }
        }
        return Component.literal(namespace);
    }
}
