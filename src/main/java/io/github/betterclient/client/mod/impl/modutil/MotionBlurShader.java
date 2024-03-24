package io.github.betterclient.client.mod.impl.modutil;

import io.github.betterclient.client.mod.impl.other.MotionBlur;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class MotionBlurShader extends Resource {

	public MotionBlurShader() {
		super(MinecraftClient.getInstance().getDefaultResourcePack(), () -> IOUtils.toInputStream(String.format(
                        """
										{
										  "targets": [
										    "swap",
										    "previous"
										  ],
										  "passes": [
										    {
										      "name": "motion_blur",
										      "intarget": "minecraft:main",
										      "outtarget": "swap",
										      "auxtargets": [
										        {
										          "name": "PrevSampler",
										          "id": "previous"
										        }
										      ],
										      "uniforms": [
										        {
										          "name": "BlendFactor",
										          "values": [
										            %s
										          ]
										        }
										      ]
										    },
										    {
										      "name": "blit",
										      "intarget": "swap",
										      "outtarget": "previous"
										    },
										    {
										      "name": "blit",
										      "intarget": "swap",
										      "outtarget": "minecraft:main"
										    }
										  ]
										}
								""", MotionBlur.get().getBlur()),
				"utf-8"));
	}
	}