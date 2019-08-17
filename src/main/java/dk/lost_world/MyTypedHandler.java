package dk.lost_world;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;


public class MyTypedHandler implements TypedActionHandler {

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {

        final Document document = editor.getDocument();

        CaretModel caretModel = editor.getCaretModel();
        document.insertString(caretModel.getOffset(), String.format("%c", charTyped));
        caretModel.moveToOffset(caretModel.getOffset() + 1);
        VisualPosition visualPosition = caretModel.getVisualPosition();

//        PsiFile psiFile = dataContext.getData(CommonDataKeys.PSI_FILE);


        Project project = editor.getProject();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Point p = editor.visualPositionToXY(new VisualPosition(visualPosition.line + 1, visualPosition.column));
                p.x = p.x + editor.getComponent().getLocationOnScreen().x + 30;
                p.y = p.y + editor.getComponent().getLocationOnScreen().y - 20;
                RelativePoint rp = RelativePoint.fromScreen(p);

                SpriteA sa = new SpriteA();
                sa.button1.setText(p.toString());
                JComponent jc = sa.getContent();

                JBPopupFactory.getInstance().createBalloonBuilder(jc)
                        .setFadeoutTime(1000)
                        .createBalloon().show(rp, Balloon.Position.above);



                Point p2 = editor.visualPositionToXY(new VisualPosition(visualPosition.line + 1, visualPosition.column));
                p2.x = p2.x + editor.getComponent().getLocationOnScreen().x + 30;
                p2.y = p2.y + editor.getComponent().getLocationOnScreen().y + 20;

                SpriteA sa2 = new SpriteA();
                sa2.button1.setText(p2.toString());
//                sa2.button2.setIcon(new ImageIcon(getClass().getResource("/cube_spin.gif")));
                sa2.button2.setIcon(new ImageIcon(getClass().getResource("/cube11.png")));

                JBPopupFactory.getInstance().createBalloonBuilder(sa2.getContent())
                        .setFadeoutTime(1000)
                        .createBalloon().show(RelativePoint.fromScreen(p2), Balloon.Position.below);


                Point p3 = editor.visualPositionToXY(new VisualPosition(visualPosition.line + 1, visualPosition.column));
                p3.x = p3.x + editor.getComponent().getLocationOnScreen().x + 300;
                p3.y = p3.y + editor.getComponent().getLocationOnScreen().y + 90;

                JLabel spriteLabel = new JLabel();
                spriteLabel.setIcon(new ImageIcon(getClass().getResource("/blender/cube/0001.png")));

                JBPopupFactory.getInstance().createBalloonBuilder(spriteLabel)
                        .setFadeoutTime(1000)
                        .createBalloon().show(RelativePoint.fromScreen(p3), Balloon.Position.below);


                Point p4 = editor.visualPositionToXY(new VisualPosition(visualPosition.line + 1, visualPosition.column));
                p4.x = p4.x + editor.getComponent().getLocationOnScreen().x + 300;
                p4.y = p4.y + editor.getComponent().getLocationOnScreen().y - 90;

                JLabel spriteLabel2 = new JLabel();
                spriteLabel2.setIcon(new ImageIcon(getClass().getResource("/blender/cube/0002.png")));
                spriteLabel2.setOpaque(false);

//                MyPanel myPanel = new MyPanel(p4);
//                JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(myPanel, spriteLabel2)
//                        .createPopup();
                //popup.getOwner().setBackground(new Color(0,0,0,0));

                JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(spriteLabel2, spriteLabel2)
                        .createPopup();





                JComponent popup_content = popup.getContent();
                popup_content.setOpaque(false);
                popup.show(RelativePoint.fromScreen((p4)));
                popup_content.getRootPane().setOpaque(false);
                popup_content.setOpaque(false);
                popup_content.setBackground(new Color(0,0,0,0));
//                spriteLabel2.setOpaque(false);


//                try {
//                    for(int i =1; i <25; i++) {
//
//                        spriteLabel2.setIcon(spriteHolder.sprites.get(i));
//                        Thread.sleep(200);
//
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

//                popup.cancel();



                // document.insertString(0, "Typed\n");
            }
        };
        WriteCommandAction.runWriteCommandAction(project, runnable);

    }
}
