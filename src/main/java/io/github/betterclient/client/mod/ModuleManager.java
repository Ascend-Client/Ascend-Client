package io.github.betterclient.client.mod;

import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.mod.impl.ClientMod;
import io.github.betterclient.client.mod.impl.hud.*;
import io.github.betterclient.client.mod.impl.other.*;
import io.github.betterclient.fabric.FabricLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

public class ModuleManager {
    private boolean isEnabled = true;
    public List<Module> moduleList  = new Vector<>();
    public static ModuleManager instance;

    public ModuleManager() {
        instance = this;
        moduleList.add(new ClientMod()); //first mod should be client itself
        moduleList.add(new FPSMod());
        moduleList.add(new KeyStrokesMod());
        moduleList.add(new CPSMod());
        moduleList.add(new ReachDisplayMod());
        moduleList.add(new ItemPhysics());
        moduleList.add(new ArmorStatusMod());
        moduleList.add(new FullBright());
        moduleList.add(new FreeLook());
        moduleList.add(new Zoom());
        moduleList.add(new NoHurtCam());
        moduleList.add(new CrystalOptimizer());
        moduleList.add(new MotionBlur());
        moduleList.add(new PingDisplayMod());
        moduleList.add(new ServerDisplayMod());
        moduleList.add(new SuperSecretSettings());
        moduleList.add(new CrosshairMod());
        moduleList.add(SmoothFont.instance);
        moduleList.add(new PositionMod());
        moduleList.add(new SnapLook());
        moduleList.add(new PaperDoll());

        IBridge.getPreLaunch().registerVersionAscendMods(this);

        this.isEnabled = true;
        try {
            FabricLoader.getInstance().callAscendMain();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.isEnabled = false;
    }

    public Module getModuleByName(String name) {
        return this.moduleList.stream().filter(it -> it.name.equalsIgnoreCase(name)).findFirst().orElseThrow();
    }

    public boolean hasModule(String name) {
        return this.moduleList.stream().anyMatch(module -> module.name.equalsIgnoreCase(name));
    }

    public List<Module> getByCategory(Category category) {
        return this.moduleList.stream().filter(it -> it.cat == category).toList();
    }

    public List<Module> getEnabledMods() {
        return this.moduleList.stream().filter(Module::isToggled).toList();
    }

    public void addModule(Module module) {
        if (!isEnabled)
            throw new IllegalStateException("Cannot add modules in current state, use the ACL initializer.");

        this.moduleList.add(module);
    }
}
