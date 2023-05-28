package io.github.betterclient.client.mod.impl.modutil;

import io.github.betterclient.client.mod.impl.other.MotionBlur;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class MotionBlurShader implements Resource {

		@Override
		public Identifier getId() {
			return null;
		}

		@Override
		public InputStream getInputStream() {
			try {
				return IOUtils.toInputStream(String.format("""
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
                        "values": [ %s ]
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
    """, MotionBlur.get().blurStrength.value / 100f), "utf-8");
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		public <T> T getMetadata(ResourceMetadataReader<T> metaReader) {
			return null;
		}

		@Override
		public String getResourcePackName() {
			return null;
		}

		@Override
		public void close() throws IOException {

		}
	}