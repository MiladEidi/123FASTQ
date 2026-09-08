package Eidi._123Fastq.GUI;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

public class MovableTabbedPane extends JTabbedPane {
    //stack traces have been comment.
    public static final long serialVersionUID = 1L;
    private static final int LINEWIDTH = 3;
    private static final String NAME = "TabTransferData";
    private final DataFlavor FLAVOR = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType, NAME);
    private static GhostGlassPane s_glassPane = new GhostGlassPane();
    private boolean m_isDrawRect = false;
    private final Rectangle2D m_lineRect = new Rectangle2D.Double();
    private final Color m_lineColor = new Color(0, 100, 255);
    private TabAcceptor m_acceptor = null;

    public MovableTabbedPane() {
        super();
        final DragSourceListener dsl = new DragSourceListener() {
            public void dragEnter(DragSourceDragEvent e) {
                e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
            }

            public void dragExit(DragSourceEvent e) {
                e.getDragSourceContext()
                        .setCursor(DragSource.DefaultMoveNoDrop);
                m_lineRect.setRect(0, 0, 0, 0);
                m_isDrawRect = false;
                s_glassPane.setPoint(new Point(-1000, -1000));
                s_glassPane.repaint();
            }

            public void dragOver(DragSourceDragEvent e) {

                TabTransferData data = getTabTransferData(e);
                if (data == null) {
                    e.getDragSourceContext().setCursor(
                            DragSource.DefaultMoveNoDrop);
                    return;
                }
                e.getDragSourceContext().setCursor(
                        DragSource.DefaultMoveDrop);
            }

            public void dragDropEnd(DragSourceDropEvent e) {
                m_isDrawRect = false;
                m_lineRect.setRect(0, 0, 0, 0);
                if (hasGhost()) {
                    s_glassPane.setVisible(false);
                    s_glassPane.setImage(null);
                }
            }

            public void dropActionChanged(DragSourceDragEvent e) {
            }
        };

        final DragGestureListener dgl = new DragGestureListener() {
            public void dragGestureRecognized(DragGestureEvent e) {
                Point tabPt = e.getDragOrigin();
                int dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
                if (dragTabIndex < 0) {
                    return;
                } // if

                initGlassPane(e.getComponent(), e.getDragOrigin(), dragTabIndex);
                try {
                    e.startDrag(DragSource.DefaultMoveDrop,
                            new TabTransferable(MovableTabbedPane.this, dragTabIndex), dsl);
                } catch (InvalidDnDOperationException idoe) {
//                    idoe.printStackTrace();
                }
            }
        };

        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,
                new CDropTargetListener(), true);
        new DragSource().createDefaultDragGestureRecognizer(this,
                DnDConstants.ACTION_COPY_OR_MOVE, dgl);
        m_acceptor = new TabAcceptor() {
            public boolean isDropAcceptable(MovableTabbedPane a_component, int a_index) {
                return true;
            }
        };
    }

    public TabAcceptor getAcceptor() {
        return m_acceptor;
    }

    public void setAcceptor(TabAcceptor a_value) {
        m_acceptor = a_value;
    }

    private TabTransferData getTabTransferData(DropTargetDropEvent a_event) {
        try {
            TabTransferData data = (TabTransferData) a_event.getTransferable().getTransferData(FLAVOR);
            return data;
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return null;
    }

    private TabTransferData getTabTransferData(DropTargetDragEvent a_event) {
        try {
            TabTransferData data = (TabTransferData) a_event.getTransferable().getTransferData(FLAVOR);
            return data;
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return null;
    }

    private TabTransferData getTabTransferData(DragSourceDragEvent a_event) {
        try {
            TabTransferData data = (TabTransferData) a_event.getDragSourceContext()
                    .getTransferable().getTransferData(FLAVOR);
            return data;
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return null;
    }

    class TabTransferable implements Transferable {

        private TabTransferData m_data = null;

        public TabTransferable(MovableTabbedPane a_tabbedPane, int a_tabIndex) {
            m_data = new TabTransferData(MovableTabbedPane.this, a_tabIndex);
        }

        public Object getTransferData(DataFlavor flavor) {
            return m_data;
        }

        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] f = new DataFlavor[1];
            f[0] = FLAVOR;
            return f;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.getHumanPresentableName().equals(NAME);
        }
    }

    class TabTransferData {

        private MovableTabbedPane m_tabbedPane = null;
        private int m_tabIndex = -1;

        public TabTransferData() {
        }

        public TabTransferData(MovableTabbedPane a_tabbedPane, int a_tabIndex) {
            m_tabbedPane = a_tabbedPane;
            m_tabIndex = a_tabIndex;
        }

        public MovableTabbedPane getTabbedPane() {
            return m_tabbedPane;
        }

        public void setTabbedPane(MovableTabbedPane pane) {
            m_tabbedPane = pane;
        }

        public int getTabIndex() {
            return m_tabIndex;
        }

        public void setTabIndex(int index) {
            m_tabIndex = index;
        }
    }

    private Point buildGhostLocation(Point a_location) {
        Point retval = new Point(a_location);

        switch (getTabPlacement()) {
            case JTabbedPane.TOP: {
                retval.y = 1;
                retval.x -= s_glassPane.getGhostWidth() / 2;
            }
            break;

            case JTabbedPane.BOTTOM: {
                retval.y = getHeight() - 1 - s_glassPane.getGhostHeight();
                retval.x -= s_glassPane.getGhostWidth() / 2;
            }
            break;

            case JTabbedPane.LEFT: {
                retval.x = 1;
                retval.y -= s_glassPane.getGhostHeight() / 2;
            }
            break;

            case JTabbedPane.RIGHT: {
                retval.x = getWidth() - 1 - s_glassPane.getGhostWidth();
                retval.y -= s_glassPane.getGhostHeight() / 2;
            }
            break;
        }

        retval = SwingUtilities.convertPoint(MovableTabbedPane.this,
                retval, s_glassPane);
        return retval;
    }

    class CDropTargetListener implements DropTargetListener {

        public void dragEnter(DropTargetDragEvent e) {
            if (isDragAcceptable(e)) {
                e.acceptDrag(e.getDropAction());
            } else {
                e.rejectDrag();
            }
        }

        public void dragExit(DropTargetEvent e) {
            m_isDrawRect = false;
        }

        public void dropActionChanged(DropTargetDragEvent e) {
        }

        public void dragOver(final DropTargetDragEvent e) {
            TabTransferData data = getTabTransferData(e);

            if (getTabPlacement() == JTabbedPane.TOP
                    || getTabPlacement() == JTabbedPane.BOTTOM) {
                initTargetLeftRightLine(getTargetTabIndex(e.getLocation()), data);
            } else {
                initTargetTopBottomLine(getTargetTabIndex(e.getLocation()), data);
            }

            repaint();
            if (hasGhost()) {
                s_glassPane.setPoint(buildGhostLocation(e.getLocation()));
                s_glassPane.repaint();
            }
        }

        public void drop(DropTargetDropEvent a_event) {
            if (isDropAcceptable(a_event)) {
                convertTab(getTabTransferData(a_event),
                        getTargetTabIndex(a_event.getLocation()));
                a_event.dropComplete(true);
            } else {
                a_event.dropComplete(false);
            }

            m_isDrawRect = false;
            repaint();
        }

        public boolean isDragAcceptable(DropTargetDragEvent e) {
            Transferable t = e.getTransferable();
            if (t == null) {
                return false;
            }

            DataFlavor[] flavor = e.getCurrentDataFlavors();
            if (!t.isDataFlavorSupported(flavor[0])) {
                return false;
            }

            TabTransferData data = getTabTransferData(e);

            if (MovableTabbedPane.this == data.getTabbedPane()
                    && data.getTabIndex() >= 0) {
                return true;
            }

            if (MovableTabbedPane.this != data.getTabbedPane()) {
                if (m_acceptor != null) {
                    return m_acceptor.isDropAcceptable(data.getTabbedPane(), data.getTabIndex());
                }
            }

            return false;
        }

        public boolean isDropAcceptable(DropTargetDropEvent e) {
            Transferable t = e.getTransferable();
            if (t == null) {
                return false;
            }
            DataFlavor[] flavor = e.getCurrentDataFlavors();
            if (!t.isDataFlavorSupported(flavor[0])) {
                return false;
            }
            TabTransferData data = getTabTransferData(e);
            if (MovableTabbedPane.this == data.getTabbedPane()
                    && data.getTabIndex() >= 0) {
                return true;
            }
            if (MovableTabbedPane.this != data.getTabbedPane()) {
                if (m_acceptor != null) {
                    return m_acceptor.isDropAcceptable(data.getTabbedPane(), data.getTabIndex());
                }
            }

            return false;
        }
    }

    private boolean m_hasGhost = true;

    public void setPaintGhost(boolean flag) {
        m_hasGhost = flag;
    }

    public boolean hasGhost() {
        return m_hasGhost;
    }

    private int getTargetTabIndex(Point a_point) {
        boolean isTopOrBottom = getTabPlacement() == JTabbedPane.TOP
                || getTabPlacement() == JTabbedPane.BOTTOM;

        if (getTabCount() == 0) {
            return 0;
        }

        for (int i = 0; i < getTabCount(); i++) {
            Rectangle r = getBoundsAt(i);
            if (isTopOrBottom) {
                r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
            } else {
                r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
            }

            if (r.contains(a_point)) {
                return i;
            }
        }

        Rectangle r = getBoundsAt(getTabCount() - 1);
        if (isTopOrBottom) {
            int x = r.x + r.width / 2;
            r.setRect(x, r.y, getWidth() - x, r.height);
        } else {
            int y = r.y + r.height / 2;
            r.setRect(r.x, y, r.width, getHeight() - y);
        }

        return r.contains(a_point) ? getTabCount() : -1;
    }

    private void convertTab(TabTransferData a_data, int a_targetIndex) {
        MovableTabbedPane source = a_data.getTabbedPane();
        int sourceIndex = a_data.getTabIndex();
        if (sourceIndex < 0) {
            return;
        }

        Component cmp = source.getComponentAt(sourceIndex);
        Component cmpEachTab = source.getTabComponentAt(sourceIndex);
        String str = source.getTitleAt(sourceIndex);

        if (this != source) {
            source.remove(sourceIndex);
            if (a_targetIndex == getTabCount()) {
                addTab(str, cmp);
                setTabComponentAt(a_targetIndex, cmpEachTab);
            } else {
                if (a_targetIndex < 0) {
                    a_targetIndex = 0;
                }
                insertTab(str, null, cmp, null, a_targetIndex);
                setTabComponentAt(a_targetIndex, cmpEachTab);
            }
            setSelectedComponent(cmp);
            return;
        }

        if (a_targetIndex < 0 || sourceIndex == a_targetIndex) {
            return;
        }

        if (a_targetIndex == getTabCount()) {
            source.remove(sourceIndex);
            addTab(str, cmp);
            setSelectedIndex(getTabCount() - 1);
            setTabComponentAt(a_targetIndex - 1, cmpEachTab);
        } else if (sourceIndex > a_targetIndex) {
            source.remove(sourceIndex);
            insertTab(str, null, cmp, null, a_targetIndex);
            setSelectedIndex(a_targetIndex);
            setTabComponentAt(a_targetIndex, cmpEachTab);
        } else {
            source.remove(sourceIndex);
            insertTab(str, null, cmp, null, a_targetIndex - 1);
            setSelectedIndex(a_targetIndex - 1);
            setTabComponentAt(a_targetIndex - 1, cmpEachTab);
        }
    }

    private void initTargetLeftRightLine(int next, TabTransferData a_data) {
        if (next < 0) {
            m_lineRect.setRect(0, 0, 0, 0);
            m_isDrawRect = false;
            return;
        }

        if ((a_data.getTabbedPane() == this)
                && (a_data.getTabIndex() == next
                || next - a_data.getTabIndex() == 1)) {
            m_lineRect.setRect(0, 0, 0, 0);
            m_isDrawRect = false;
        } else if (getTabCount() == 0) {
            m_lineRect.setRect(0, 0, 0, 0);
            m_isDrawRect = false;
        } else if (next == 0) {
            Rectangle rect = getBoundsAt(0);
            m_lineRect.setRect(-LINEWIDTH / 2, rect.y, LINEWIDTH, rect.height);
            m_isDrawRect = true;
        } else if (next == getTabCount()) {
            Rectangle rect = getBoundsAt(getTabCount() - 1);
            m_lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y,
                    LINEWIDTH, rect.height);
            m_isDrawRect = true;
        } else {
            Rectangle rect = getBoundsAt(next - 1);
            m_lineRect.setRect(rect.x + rect.width - LINEWIDTH / 2, rect.y,
                    LINEWIDTH, rect.height);
            m_isDrawRect = true;
        }
    }

    private void initTargetTopBottomLine(int next, TabTransferData a_data) {
        if (next < 0) {
            m_lineRect.setRect(0, 0, 0, 0);
            m_isDrawRect = false;
            return;
        }
        if ((a_data.getTabbedPane() == this)
                && (a_data.getTabIndex() == next
                || next - a_data.getTabIndex() == 1)) {
            m_lineRect.setRect(0, 0, 0, 0);
            m_isDrawRect = false;
        } else if (getTabCount() == 0) {
            m_lineRect.setRect(0, 0, 0, 0);
            m_isDrawRect = false;
            return;
        } else if (next == getTabCount()) {
            Rectangle rect = getBoundsAt(getTabCount() - 1);
            m_lineRect.setRect(rect.x, rect.y + rect.height - LINEWIDTH / 2,
                    rect.width, LINEWIDTH);
            m_isDrawRect = true;
        } else if (next == 0) {
            Rectangle rect = getBoundsAt(0);
            m_lineRect.setRect(rect.x, -LINEWIDTH / 2, rect.width, LINEWIDTH);
            m_isDrawRect = true;
        } else {
            Rectangle rect = getBoundsAt(next - 1);
            m_lineRect.setRect(rect.x, rect.y + rect.height - LINEWIDTH / 2,
                    rect.width, LINEWIDTH);
            m_isDrawRect = true;
        }
    }

    private void initGlassPane(Component c, Point tabPt, int a_tabIndex) {
        //Point p = (Point) pt.clone();
        getRootPane().setGlassPane(s_glassPane);
        if (hasGhost()) {
            Rectangle rect = getBoundsAt(a_tabIndex);
            BufferedImage image = new BufferedImage(c.getWidth(),
                    c.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = image.getGraphics();
            c.paint(g);
            image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            s_glassPane.setImage(image);
        }

        s_glassPane.setPoint(buildGhostLocation(tabPt));
        s_glassPane.setVisible(true);
    }

    private Rectangle getTabAreaBound() {
        Rectangle lastTab = getUI().getTabBounds(this, getTabCount() - 1);
        return new Rectangle(0, 0, getWidth(), lastTab.y + lastTab.height);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (m_isDrawRect) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(m_lineColor);
            g2.fill(m_lineRect);
        }
    }

    public interface TabAcceptor {

        boolean isDropAcceptable(MovableTabbedPane a_component, int a_index);
    }
}

class GhostGlassPane extends JPanel {

    public static final long serialVersionUID = 1L;
    private final AlphaComposite m_composite;

    private Point m_location = new Point(0, 0);

    private BufferedImage m_draggingGhost = null;

    public GhostGlassPane() {
        setOpaque(false);
        m_composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
    }

    public void setImage(BufferedImage draggingGhost) {
        m_draggingGhost = draggingGhost;
    }

    public void setPoint(Point a_location) {
        m_location.x = a_location.x;
        m_location.y = a_location.y;
    }

    public int getGhostWidth() {
        if (m_draggingGhost == null) {
            return 0;
        }
        return m_draggingGhost.getWidth(this);
    }

    public int getGhostHeight() {
        if (m_draggingGhost == null) {
            return 0;
        }
        return m_draggingGhost.getHeight(this);
    }

    public void paintComponent(Graphics g) {
        if (m_draggingGhost == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(m_composite);
        g2.drawImage(m_draggingGhost, (int) m_location.getX(), (int) m_location.getY(), null);
    }
}
