
/*
 * Copyright 2015 Baptiste Mesta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cschar.pmode3;

import java.awt.*;

public class ParticleSpriteLizard extends Particle{

    public ParticleSpriteLizard(int x, int y, int dx, int dy, int size, int life, Color c) {
        super(x,y,dx,dy,size,life,c);

        //TODO
        ///ues PSI to get outer class and send particle from caret to class xy position
        //https://github.com/JetBrains/intellij-sdk-docs/blob/master/code_samples/psi_demo/src/com/intellij/tutorials/psi/PsiNavigationDemoAction.java

        //Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        //PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        //if (editor == null || psiFile == null) return;
        //int offset = editor.getCaretModel().getOffset();

        //final StringBuilder infoBuilder = new StringBuilder();
        //PsiElement element = psiFile.findElementAt(offset);
        //infoBuilder.append("Element at caret: ").append(element).append("\n");
//        if (element != null) {
//            PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
//            infoBuilder
//                    .append("Containing method: ")
//                    .append(containingMethod != null ? containingMethod.getName() : "none")
//                    .append("\n");
//            if (containingMethod != null) {
//                PsiClass containingClass = containingMethod.getContainingClass();
//                infoBuilder
//                        .append("Containing class: ")
//                        .append(containingClass != null ? containingClass.getName() : "none")
//                        .append("\n");
//
//                infoBuilder.append("Local variables:\n");
//                containingMethod.accept(new JavaRecursiveElementVisitor() {
//                    @Override
//                    public void visitLocalVariable(PsiLocalVariable variable) {
//                        super.visitLocalVariable(variable);
//                        infoBuilder.append(variable.getName()).append("\n");
//                    }
//                });
//            }
//        }

    }

    public boolean update() {
        x += dx;
        y += dy;
        life--;
        return life <= 0;
    }

    public void render(Graphics g) {
        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);
            g2d.fillRect(x - (size / 2), y - (size / 2), size, size);
            g2d.dispose();
        }
    }

    @Override
    public String toString() {
        return "Particle{" +
                "x=" + x +
                ", y=" + y +
                ", dx=" + dx +
                ", dy=" + dy +
                ", size=" + size +
                ", life=" + life +
                ", c=" + c +
                '}';
    }
}
