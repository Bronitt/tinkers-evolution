package xyz.phanta.tconevo.integration;

import com.google.common.collect.Sets;
import io.github.phantamanta44.libnine.LibNine;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import xyz.phanta.tconevo.TconEvoConfig;
import xyz.phanta.tconevo.TconEvoMod;
import xyz.phanta.tconevo.integration.iu.IUHooks;
import xyz.phanta.tconevo.integration.iu.IUHooksImpl;
import xyz.phanta.tconevo.util.ReflectionHackUtils;

import java.lang.reflect.Field;
import java.util.*;

public class IntegrationManager {

    private static final Set<String> blacklisted = Sets.newHashSet(TconEvoConfig.disabledModHooks);
    private static final List<IntegrationHooks> hooksInstances = new ArrayList<>();

    public static void injectHooks(ASMDataTable annotTable) {

        //FIXME
        IUHooksImpl.LOGGER.info(IntegrationHooks.Inject.class.getName() + " - " + IUHooks.INSTANCE.getClass().getName());
        try {
            ASMDataTable.ASMData an = null;
            for (ASMDataTable.ASMData annotation : annotTable.getAll(IntegrationHooks.Inject.class.getName())) {
                an = annotation;
                break;
            }
            Map<String, Object> annotationInfo = new HashMap<>();
            annotationInfo.put("value", (Object) IUHooksImpl.MOD_ID);
            IUHooksImpl.LOGGER.info(annotationInfo + " - " + IUHooksImpl.MOD_ID + annotationInfo.get("value").getClass().getName());
            annotTable.addASMData(an.getCandidate(), "xyz.phanta.tconevo.integration.IntegrationHooks$Inject", "xyz.phanta.tconevo.integration.iu.IUHooks", "INSTANCE", annotationInfo);
        } catch (NullPointerException e) {TconEvoMod.LOGGER.info("annotation nullable: ", e);}

        for (ASMDataTable.ASMData annot : annotTable.getAll(IntegrationHooks.Inject.class.getName())) {
            String modId = (String)annot.getAnnotationInfo().get("value");
            if (!Loader.isModLoaded(modId)) {
                TconEvoMod.LOGGER.info("Ignoring integration for missing mod: {}", modId);
            } else if (blacklisted.contains(modId)) {
                TconEvoMod.LOGGER.info("Ignoring disabled integration for mod: {}", modId);
            } else {
                TconEvoMod.LOGGER.info("Loading integration for mod: {}", modId);
                try {
                    IUHooksImpl.LOGGER.info(annot.getClassName() + " - " + annot.getObjectName());
                    Field fHooksImpl = Class.forName(annot.getClassName()).getField(annot.getObjectName());
                    IUHooksImpl.LOGGER.info(fHooksImpl.getName() + " - " + fHooksImpl);
                    ReflectionHackUtils.forceWritable(fHooksImpl);
                    IUHooksImpl.LOGGER.info(fHooksImpl);
                    Object hooksImpl = Class.forName(getImplClass(annot)).newInstance();
                    IUHooksImpl.LOGGER.info(hooksImpl + " - " + hooksImpl.getClass() + " - " + hooksImpl.getClass().getName());
                    fHooksImpl.set(null, hooksImpl);
                    IUHooksImpl.LOGGER.info(hooksImpl.getClass() + " - " + hooksImpl.getClass().getName());
                    if (hooksImpl instanceof IntegrationHooks) {
                        hooksInstances.add((IntegrationHooks)hooksImpl);
                        IUHooksImpl.LOGGER.info("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                    }
                } catch (Exception e) {
                    TconEvoMod.LOGGER.error("Failed to load integration: " + modId, e);
                } catch (Error e) {
                    throw new Error("Failed to load integration: " + modId, e);
                }
            }
        }
    }

    private static String getImplClass(ASMDataTable.ASMData annot) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT
                && annot.getAnnotationInfo().containsKey("sided") && (boolean)annot.getAnnotationInfo().get("sided")) {
            String ifcClass = annot.getClassName();
            int classNdx = ifcClass.lastIndexOf('.'); // we will assume there is at least one dot (i.e. not default package)
            return ifcClass.substring(0, classNdx) + ".client." + ifcClass.substring(classNdx + 1) + "ClientImpl";
        } else {
            return annot.getClassName() + "Impl";
        }
    }

    public static void dispatchPreInit(FMLPreInitializationEvent event) {
        LibNine.PROXY.getRegistrar().begin(TconEvoMod.INSTANCE);
        for (IntegrationHooks hooksImpl : hooksInstances) {
            hooksImpl.doRegistration();
        }
        LibNine.PROXY.getRegistrar().end();
        for (IntegrationHooks hooksImpl : hooksInstances) {
            hooksImpl.onPreInit(event);
        }
    }

    public static void dispatchInit(FMLInitializationEvent event) {
        for (IntegrationHooks hooksImpl : hooksInstances) {
            hooksImpl.onInit(event);
        }
    }

    public static void dispatchPostInit(FMLPostInitializationEvent event) {
        for (IntegrationHooks hooksImpl : hooksInstances) {
            hooksImpl.onPostInit(event);
        }
    }

}
