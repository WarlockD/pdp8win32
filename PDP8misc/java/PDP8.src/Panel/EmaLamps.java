/*  1:   */ package Panel;
/*  2:   */ 
/*  3:   */ import java.awt.Dimension;
/*  4:   */ import java.awt.GridBagConstraints;
/*  5:   */ import java.awt.GridBagLayout;
/*  6:   */ import javax.swing.JPanel;
/*  7:   */ 
/*  8:   */ public class EmaLamps
/*  9:   */   extends JPanel
/* 10:   */ {
/* 11:   */   private Lamp emaLamp0;
/* 12:   */   private Lamp emaLamp1;
/* 13:   */   private Lamp emaLamp2;
/* 14:   */   
/* 15:   */   public EmaLamps()
/* 16:   */   {
/* 17:18 */     initComponents();
/* 18:   */   }
/* 19:   */   
/* 20:   */   private void initComponents()
/* 21:   */   {
/* 22:27 */     this.emaLamp0 = new Lamp();
/* 23:28 */     this.emaLamp1 = new Lamp();
/* 24:29 */     this.emaLamp2 = new Lamp();
/* 25:   */     
/* 26:31 */     setLayout(new GridBagLayout());
/* 27:   */     
/* 28:33 */     setMaximumSize(new Dimension(78, 10));
/* 29:34 */     setMinimumSize(new Dimension(39, 5));
/* 30:35 */     setPreferredSize(new Dimension(78, 10));
/* 31:36 */     this.emaLamp0.setMaximumSize(new Dimension(26, 10));
/* 32:37 */     this.emaLamp0.setMinimumSize(new Dimension(13, 5));
/* 33:38 */     this.emaLamp0.setPreferredSize(new Dimension(26, 10));
/* 34:39 */     add(this.emaLamp0, new GridBagConstraints());
/* 35:   */     
/* 36:41 */     this.emaLamp1.setMaximumSize(new Dimension(26, 10));
/* 37:42 */     this.emaLamp1.setMinimumSize(new Dimension(13, 5));
/* 38:43 */     this.emaLamp1.setPreferredSize(new Dimension(26, 10));
/* 39:44 */     add(this.emaLamp1, new GridBagConstraints());
/* 40:   */     
/* 41:46 */     this.emaLamp2.setMaximumSize(new Dimension(26, 10));
/* 42:47 */     this.emaLamp2.setMinimumSize(new Dimension(13, 5));
/* 43:48 */     this.emaLamp2.setPreferredSize(new Dimension(26, 10));
/* 44:49 */     add(this.emaLamp2, new GridBagConstraints());
/* 45:   */   }
/* 46:   */   
/* 47:   */   public void setState(int state)
/* 48:   */   {
/* 49:54 */     this.state = state;
/* 50:55 */     this.emaLamp0.setState(state & 0x4);
/* 51:56 */     this.emaLamp1.setState(state & 0x2);
/* 52:57 */     this.emaLamp2.setState(state & 0x1);
/* 53:   */   }
/* 54:   */   
/* 55:67 */   private int state = -1;
/* 56:   */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Panel.EmaLamps
 * JD-Core Version:    0.7.0.1
 */