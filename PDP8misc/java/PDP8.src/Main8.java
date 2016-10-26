/*  1:   */ import Logic.BusRegMem;
/*  2:   */ import Logic.PanelLogic;
/*  3:   */ import Logic.Proc;
/*  4:   */ import Panel.Panel8;
/*  5:   */ import java.awt.Container;
/*  6:   */ import java.awt.GridBagConstraints;
/*  7:   */ import java.awt.GridBagLayout;
/*  8:   */ import java.awt.event.WindowAdapter;
/*  9:   */ import java.awt.event.WindowEvent;
/* 10:   */ import javax.swing.JFrame;
/* 11:   */ 
/* 12:   */ public class Main8
/* 13:   */   extends JFrame
/* 14:   */ {
/* 15:   */   public Panel8 panel8;
/* 16:   */   public PanelLogic panelLogic;
/* 17:   */   public BusRegMem data;
/* 18:   */   public Proc proc;
/* 19:   */   
/* 20:   */   public Main8()
/* 21:   */   {
/* 22:17 */     initComponents();
/* 23:18 */     this.data = new BusRegMem();
/* 24:19 */     this.panelLogic = new PanelLogic(this.data, "Panel");
/* 25:20 */     this.panel8.setLogic(this.panelLogic);
/* 26:21 */     this.panelLogic.setPanel(this.panel8);
/* 27:22 */     this.panelLogic.start();
/* 28:23 */     this.proc = new Proc(this.data, "Processor");
/* 29:24 */     this.proc.start();
/* 30:   */   }
/* 31:   */   
/* 32:   */   private void initComponents()
/* 33:   */   {
/* 34:35 */     this.panel8 = new Panel8();
/* 35:   */     
/* 36:37 */     setTitle("PDP-8/E");
/* 37:38 */     addWindowListener(new WindowAdapter()
/* 38:   */     {
/* 39:   */       public void windowClosing(WindowEvent evt)
/* 40:   */       {
/* 41:40 */         Main8.this.exitForm(evt);
/* 42:   */       }
/* 43:42 */     });
/* 44:43 */     getContentPane().setLayout(new GridBagLayout());
/* 45:44 */     getContentPane().add(this.panel8, new GridBagConstraints());
/* 46:   */     
/* 47:46 */     pack();
/* 48:   */   }
/* 49:   */   
/* 50:   */   private void exitForm(WindowEvent evt)
/* 51:   */   {
/* 52:51 */     this.data.CloseAllDevs();
/* 53:   */   }
/* 54:   */   
/* 55:   */   public static void main(String[] args)
/* 56:   */   {
/* 57:59 */     new Main8().setVisible(true);
/* 58:   */   }
/* 59:   */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Main8
 * JD-Core Version:    0.7.0.1
 */