package io.github.betterclient.client.mod.impl.modutil;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.impl.other.MotionBlur;

public class MotionBlurShader extends IBridge.Resource {

	public MotionBlurShader() {
		super(() -> IBridge.Resource.toInputStream(String.format(
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