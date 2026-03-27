package view.screens;

import core.GameState;
import main.MainGame;
import utils.Assets;
import utils.UiScale;
import view.components.BackgroundPanel;
import view.components.DialogueUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class Floor1Panel extends JPanel {

    CardLayout areaLayout;
    JPanel areaContainer;
    DialogueUI dialogueUI;
    JPanel inputBlocker;

    public Floor1Panel() {

        setLayout(null);

        dialogueUI = new DialogueUI();

        areaLayout = new CardLayout();
        areaContainer = new JPanel(areaLayout);

        inputBlocker = new JPanel();
        inputBlocker.setLayout(null);
        inputBlocker.setOpaque(false);
        inputBlocker.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);
        inputBlocker.setVisible(false);
        inputBlocker.addMouseListener(new MouseAdapter() {});

        dialogueUI.setVisibilityListener(visible -> {
            inputBlocker.setVisible(visible);
            setButtonsEnabled(areaContainer, !visible);
            MainGame.getInstance().setHudEnabled(!visible);
            areaContainer.revalidate();
            areaContainer.repaint();
        });

        JPanel part1A = createPart1A();
        JPanel part1B = createPart1B();
        JPanel part1C = createPart1C();

        areaContainer.add(part1A, "PART1A");
        areaContainer.add(part1B, "PART1B");
        areaContainer.add(part1C, "PART1C");
        areaContainer.setBounds(0, 0, UiScale.GAME_WIDTH, UiScale.GAME_HEIGHT);

        add(dialogueUI, 0);
        add(inputBlocker, 1);
        add(areaContainer, 2);
    }

    private void setButtonsEnabled(Container root, boolean enabled) {
        for (Component c : root.getComponents()) {
            if (c instanceof AbstractButton || c instanceof JLabel) {
                c.setEnabled(enabled);
            }
            if (c instanceof Container) {
                setButtonsEnabled((Container) c, enabled);
            }
        }
    }

    private void addClue(String clueName) {
        GameState.getInstance().addClue(clueName);
        checkPuzzleTrigger();
    }

    private void checkPuzzleTrigger() {
        GameState gs = GameState.getInstance();
        if (gs.hasClue("Harper Testimony") && gs.hasClue("Doyle Statement")) {
            int roomClues = 0;
            if (gs.hasClue("Distorted Reflection")) roomClues++;
            if (gs.hasClue("Signs of Struggle")) roomClues++;
            if (gs.hasClue("Therapy Notes Fragment")) roomClues++;
            if (gs.hasClue("Sealed Window")) roomClues++;

            if (roomClues >= 2) {
                String[] triggerDialogue = {
                        "Something doesn’t add up...",
                        "If no one entered or left…",
                        "Then how did this happen?",
                        "[PUZZLE UNLOCKED]"
                };
                dialogueUI.startDialogue(triggerDialogue, () -> MainGame.getInstance().openPuzzle("Mystery of Room 217", "FLOOR1"));
            }
        }
    }

    private JLabel createInteractionLabel(int x, int y, int w, int h, String title, Runnable talk, Runnable examine) {
        JLabel label = new JLabel();
        label.setBounds(UiScale.x(x), UiScale.y(y), UiScale.w(w), UiScale.h(h));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // label.setBorder(BorderFactory.createLineBorder(Color.RED)); // Debug
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (label.isEnabled()) {
                    dialogueUI.showInteractionMenu(title, talk, examine);
                }
            }
        });
        return label;
    }

    private JButton createArrowButton(String arrowImagePath, int baseX, int baseY, int baseSize) {
        ImageIcon normalIcon = resizeIcon(arrowImagePath, UiScale.s(baseSize), UiScale.s(baseSize));
        ImageIcon hoverIcon = resizeIcon(arrowImagePath, UiScale.s(baseSize + 8), UiScale.s(baseSize + 8));
        JButton btn = new JButton(normalIcon);
        btn.setBounds(UiScale.x(baseX), UiScale.y(baseY), normalIcon.getIconWidth(), normalIcon.getIconHeight());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("normalIcon", normalIcon);
        btn.putClientProperty("hoverIcon", hoverIcon);
        btn.putClientProperty("origBounds", btn.getBounds());

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (!btn.isEnabled()) return;
                Rectangle orig = (Rectangle) btn.getClientProperty("origBounds");
                ImageIcon hi = (ImageIcon) btn.getClientProperty("hoverIcon");
                btn.setIcon(hi);
                int dx = (hi.getIconWidth() - orig.width) / 2;
                int dy = (hi.getIconHeight() - orig.height) / 2;
                btn.setBounds(orig.x - dx, orig.y - dy, hi.getIconWidth(), hi.getIconHeight());
                btn.repaint();
            }

            public void mouseExited(MouseEvent evt) {
                Rectangle orig = (Rectangle) btn.getClientProperty("origBounds");
                ImageIcon ni = (ImageIcon) btn.getClientProperty("normalIcon");
                btn.setIcon(ni);
                btn.setBounds(orig);
                btn.repaint();
            }
        });
        return btn;
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon original = new ImageIcon(path);
        Image img = original.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private JPanel createPart1A() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part1A.jpg"));

        // NPC 1: Ms. Harper
        JLabel harper = createInteractionLabel(150, 200, 80, 150, "MS. HARPER",
            () -> dialogueUI.startDialogue(new String[]{
                "Ms. Harper: I saw someone leave that room… I’m sure of it.",
                "Ms. Harper: They were wearing something dark… I think…",
                "Ms. Harper: ...or maybe I didn’t see clearly.",
                "[CLUE FOUND: Harper Testimony]"
            }, () -> addClue("Harper Testimony")),
            () -> dialogueUI.startDialogue(new String[]{"Ms. Harper looks nervous, her hands are trembling as she clutches her shawl."})
        );
        panel.add(harper);

        // NPC 2: Mr. Doyle
        JLabel doyle = createInteractionLabel(650, 200, 80, 150, "MR. DOYLE",
            () -> dialogueUI.startDialogue(new String[]{
                "Mr. Doyle: No one came in or out.",
                "Mr. Doyle: I’ve been here all night.",
                "Mr. Doyle: You should reconsider your assumptions, detective.",
                "[CLUE FOUND: Doyle Statement]"
            }, () -> addClue("Doyle Statement")),
            () -> dialogueUI.startDialogue(new String[]{"Mr. Doyle is leaning against the wall with a stoic expression, his eyes fixed on the hallway."})
        );
        panel.add(doyle);

        // Door 217
        JLabel door217 = createInteractionLabel(400, 150, 100, 200, "DOOR 217",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "It’s locked… from the inside.",
                "[CLUE FOUND: Room Locked From Inside]"
            }, () -> addClue("Room Locked From Inside"))
        );
        panel.add(door217);

        // Floor Stain
        JLabel floorStain = createInteractionLabel(420, 400, 150, 50, "FLOOR STAIN",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "Something was dragged… recently.",
                "[CLUE FOUND: Strange Drag Mark]"
            }, () -> addClue("Strange Drag Mark"))
        );
        panel.add(floorStain);

        // Navigation
        JButton upArrow = createArrowButton(Assets.img("upArrow.png"), 425, 20, 50);
        panel.add(upArrow);
        upArrow.addActionListener(e -> areaLayout.show(areaContainer, "PART1B"));

        JButton leftArrow = createArrowButton(Assets.img("leftArrow.png"), 30, 250, 50);
        panel.add(leftArrow);
        leftArrow.addActionListener(e -> areaLayout.show(areaContainer, "PART1C"));

        JButton downArrow = createArrowButton(Assets.img("downArrow.png"), 425, 520, 50);
        panel.add(downArrow);
        downArrow.addActionListener(e -> MainGame.getInstance().switchFloor("LOBBY"));

        return panel;
    }

    private JPanel createPart1B() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part1B.jpg"));

        // Mirror
        JLabel mirror = createInteractionLabel(100, 100, 120, 180, "MIRROR",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "My reflection feels… delayed.",
                "...no, that’s not possible.",
                "[CLUE FOUND: Distorted Reflection]"
            }, () -> addClue("Distorted Reflection"))
        );
        panel.add(mirror);

        // Bed
        JLabel bed = createInteractionLabel(300, 300, 400, 200, "BED",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "Signs of struggle.",
                "But no blood...",
                "[CLUE FOUND: Signs of Struggle]"
            }, () -> addClue("Signs of Struggle"))
        );
        panel.add(bed);

        // Desk
        JLabel desk = createInteractionLabel(750, 250, 150, 150, "DESK",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "Patient Log – Session Notes",
                "...subject shows signs of denial...",
                "...memory reconstruction unstable...",
                "[CLUE FOUND: Therapy Notes Fragment]"
            }, () -> addClue("Therapy Notes Fragment"))
        );
        panel.add(desk);

        // Window
        JLabel window = createInteractionLabel(500, 50, 150, 200, "WINDOW",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "Locked from the inside.",
                "No forced entry.",
                "[CLUE FOUND: Sealed Window]"
            }, () -> addClue("Sealed Window"))
        );
        panel.add(window);

        // Navigation
        JButton downArrow = createArrowButton(Assets.img("downArrow.png"), 425, 520, 50);
        panel.add(downArrow);
        downArrow.addActionListener(e -> areaLayout.show(areaContainer, "PART1A"));

        return panel;
    }

    private JPanel createPart1C() {
        BackgroundPanel panel = new BackgroundPanel(Assets.img("part1C.jpg"));

        // Cleaning Cart
        JLabel cart = createInteractionLabel(200, 300, 200, 150, "CLEANING CART",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "A cleaning cart… something is missing.",
                "[CLUE FOUND: Missing Cleaning Tool]"
            }, () -> addClue("Missing Cleaning Tool"))
        );
        panel.add(cart);

        // Trash Bin
        JLabel trash = createInteractionLabel(700, 450, 100, 100, "TRASH BIN",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "A torn piece of paper…",
                "You shouldn't be here.",
                "[CLUE FOUND: Destroyed Note]"
            }, () -> addClue("Destroyed Note"))
        );
        panel.add(trash);

        // Sink
        JLabel sink = createInteractionLabel(500, 250, 150, 100, "SINK",
            null,
            () -> dialogueUI.startDialogue(new String[]{
                "Faint stains… washed away.",
                "[CLUE FOUND: Cleaned Evidence]"
            }, () -> addClue("Cleaned Evidence"))
        );
        panel.add(sink);

        // Navigation
        JButton rightArrow = createArrowButton(Assets.img("rightArrow.png"), 820, 250, 50);
        panel.add(rightArrow);
        rightArrow.addActionListener(e -> areaLayout.show(areaContainer, "PART1A"));

        return panel;
    }
}
