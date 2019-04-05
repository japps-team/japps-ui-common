/*
 * Copyright (C) 2019 Williams Lopez - JApps
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package japps.ui.config;

import japps.ui.component.Button;
import japps.ui.component.Label;
import japps.ui.component.OrderElementsField;
import japps.ui.component.Panel;
import japps.ui.util.Resources;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.SwingConstants;

/**
 *
 * @author Williams Lopez - JApps
 */
public class PListComponent extends Panel {

    private OrderElementsField<PObject> orderElement;
    private Button btnAdd;
    private Button btnRemove;
    private PObject.PList plist;
    private Class clazz;
    private PObjectEditor parent;

    public PListComponent(PObject.PList plist, PObjectEditor parent) {
        this.plist = plist;
        this.parent = parent;

        orderElement = new OrderElementsField<PObject>() {
            @Override
            protected void renderElement(int row, int column, Label elementLabel, PObject element) {
                super.renderElement(row, column, elementLabel, element);
                elementLabel.setMinimumSize(new Dimension(25, 25));
                elementLabel.setMaximumSize(new Dimension(1000, 35));
            }
        };

        orderElement.addElementListener(new OrderElementsField.ElementListener<PObject>() {

            @Override
            public void clicked(PObject element) {
                parent.launchChildProperties(element, parent.getIdConfig() + "." + plist.key());
                refresh();
            }

            @Override
            public void positionChanged(PObject e1, PObject e2) {
                if (e1 == null || e2 == null) {
                    return;
                }
                plist.swap(e1, e2);
                refresh();
            }

        });

        btnAdd = new Button();
        btnAdd.setToolTipText(Resources.$("Add new element to this list"));
        btnAdd.setIcon("add-circle.png", 20, 20);
        btnAdd.setHorizontalAlignment(SwingConstants.RIGHT);
        btnAdd.addActionListener((e) -> {
            plist.addNew();
            refresh();
        });

        btnRemove = new Button();
        btnRemove.setToolTipText(Resources.$("Remove an element from this list"));
        btnRemove.setIcon("remove.png", 20, 20);
        btnRemove.setHorizontalAlignment(SwingConstants.LEFT);
        btnRemove.addActionListener((e) -> {
            plist.remove(orderElement.getSelectedElement());
            refresh();
        });

        setComponents(new Component[][]{{btnAdd, btnRemove}, {orderElement, orderElement}},
                new String[]{Panel.RIGHT + "," + Panel.GROW + "," + Panel.FILL, Panel.LEFT + "," + Panel.GROW + "," + Panel.FILL},
                new String[]{"25:25:25", Panel.FILL_GROW_CENTER});

        refresh();
    }

    public void refresh() {
        PObject[][] obj = new PObject[plist.size()][1];
        int index = 0;
        for (Object o : plist) {
            obj[index][0] = (PObject) o;
            index++;
        }
        orderElement.setValue(obj);
    }
}
