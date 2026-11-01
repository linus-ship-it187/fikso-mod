package de.jumpscare;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.Random;

public class JumpscareMod implements ClientModInitializer {

    // ===== Einstellungen =====
    // Zeit zwischen zwei Jumpscares (Sekunden, zufaellig dazwischen)
    private static final int MIN_SECONDS = 300;   // 5 Minuten
    private static final int MAX_SECONDS = 1800;  // 30 Minuten
    // Dauer des Bildes in Ticks (20 Ticks = 1 Sekunde)
    private static final int SHOW_TICKS = 40;
    // Lautstaerke des Sounds
    private static final float VOLUME = 1.0f;
    // =========================

    private static final Identifier FACE = Identifier.fromNamespaceAndPath("jumpscare", "textures/gui/face.png");
    private static final int TEX_W = 149;
    private static final int TEX_H = 165;
    private static final SoundEvent SCARE =
            SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("jumpscare", "scare"));

    private final Random random = new Random();
    private int ticksUntilScare = nextDelay();
    private int showTicksLeft = 0;

    private int nextDelay() {
        return (MIN_SECONDS + random.nextInt(MAX_SECONDS - MIN_SECONDS + 1)) * 20;
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;
            if (client.isPaused()) return;

            if (showTicksLeft > 0) {
                showTicksLeft--;
                return;
            }
            if (--ticksUntilScare <= 0) {
                trigger(client);
                ticksUntilScare = nextDelay();
            }
        });

        // Testbefehl: /jumpscare loest sofort einen Jumpscare aus
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) ->
                dispatcher.register(ClientCommands.literal("jumpscare").executes(context -> {
                    trigger(Minecraft.getInstance());
                    return 1;
                }))
        );

        // ganz oben auf dem HUD zeichnen (nach den Untertiteln = letzte Ebene)
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.SUBTITLES,
                Identifier.fromNamespaceAndPath("jumpscare", "face"),
                this::extract
        );
    }

    private void trigger(Minecraft client) {
        showTicksLeft = SHOW_TICKS;
        client.getSoundManager().play(SimpleSoundInstance.forUI(SCARE, 1.0f, VOLUME));
    }

    private void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (showTicksLeft <= 0) return;

        Window window = Minecraft.getInstance().getWindow();
        int sw = window.getGuiScaledWidth();
        int sh = window.getGuiScaledHeight();

        // Bild auf Bildschirmhoehe skalieren, Seitenverhaeltnis behalten
        int h = sh;
        int w = (int) (sh * (TEX_W / (float) TEX_H));
        int x = (sw - w) / 2;

        // Wackeln
        int shakeX = random.nextInt(9) - 4;
        int shakeY = random.nextInt(9) - 4;

        // schwarzer Hintergrund
        graphics.fill(0, 0, sw, sh, 0xFF000000);
        graphics.blit(RenderPipelines.GUI_TEXTURED, FACE, x + shakeX, shakeY, 0, 0, w, h, TEX_W, TEX_H, TEX_W, TEX_H);

        // kurzer roter Blitz am Anfang
        if (showTicksLeft > SHOW_TICKS - 3) {
            graphics.fill(0, 0, sw, sh, 0x66FF0000);
        }
    }
}
