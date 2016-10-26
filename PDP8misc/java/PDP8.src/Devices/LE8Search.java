/*   1:    */ package Devices;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridBagConstraints;
/*   6:    */ import java.awt.GridBagLayout;
/*   7:    */ import java.awt.Insets;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import javax.swing.GroupLayout;
/*  12:    */ import javax.swing.GroupLayout.Alignment;
/*  13:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  14:    */ import javax.swing.GroupLayout.SequentialGroup;
/*  15:    */ import javax.swing.JButton;
/*  16:    */ import javax.swing.JFrame;
/*  17:    */ import javax.swing.JLabel;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.JTextField;
/*  20:    */ import javax.swing.UIManager;
/*  21:    */ import javax.swing.text.BadLocationException;
/*  22:    */ import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
/*  23:    */ import javax.swing.text.Document;
/*  24:    */ import javax.swing.text.Highlighter;
/*  25:    */ import javax.swing.text.Highlighter.Highlight;
/*  26:    */ import javax.swing.text.Highlighter.HighlightPainter;
/*  27:    */ import javax.swing.text.JTextComponent;
/*  28:    */ 
/*  29:    */ public class LE8Search
/*  30:    */   extends JFrame
/*  31:    */ {
/*  32:    */   private JTextComponent textComp;
/*  33:    */   
/*  34:    */   public LE8Search()
/*  35:    */   {
/*  36: 32 */     initComponents();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setTextComponent(JTextComponent textComp)
/*  40:    */   {
/*  41: 36 */     this.textComp = textComp;
/*  42: 37 */     this.textComp.setCaretColor(Color.red);
/*  43:    */   }
/*  44:    */   
/*  45:    */   private void initComponents()
/*  46:    */   {
/*  47: 46 */     this.jPanel1 = new JPanel();
/*  48: 47 */     this.jLabel1 = new JLabel();
/*  49: 48 */     this.jTextField1 = new JTextField();
/*  50: 49 */     this.jButton1 = new JButton();
/*  51: 50 */     this.jLabel2 = new JLabel();
/*  52:    */     
/*  53: 52 */     setDefaultCloseOperation(2);
/*  54:    */     
/*  55: 54 */     this.jPanel1.setLayout(new GridBagLayout());
/*  56:    */     
/*  57: 56 */     this.jLabel1.setHorizontalAlignment(11);
/*  58: 57 */     this.jLabel1.setText("Text to Find:");
/*  59: 58 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*  60: 59 */     gridBagConstraints.gridx = 0;
/*  61: 60 */     gridBagConstraints.gridy = 0;
/*  62: 61 */     gridBagConstraints.fill = 3;
/*  63: 62 */     gridBagConstraints.ipadx = 60;
/*  64: 63 */     gridBagConstraints.ipady = 6;
/*  65: 64 */     gridBagConstraints.anchor = 18;
/*  66: 65 */     gridBagConstraints.insets = new Insets(4, 4, 4, 4);
/*  67: 66 */     this.jPanel1.add(this.jLabel1, gridBagConstraints);
/*  68: 67 */     gridBagConstraints = new GridBagConstraints();
/*  69: 68 */     gridBagConstraints.gridx = 1;
/*  70: 69 */     gridBagConstraints.gridy = 0;
/*  71: 70 */     gridBagConstraints.fill = 1;
/*  72: 71 */     gridBagConstraints.ipadx = 60;
/*  73: 72 */     gridBagConstraints.ipady = 4;
/*  74: 73 */     gridBagConstraints.anchor = 18;
/*  75: 74 */     gridBagConstraints.insets = new Insets(4, 4, 4, 4);
/*  76: 75 */     this.jPanel1.add(this.jTextField1, gridBagConstraints);
/*  77:    */     
/*  78: 77 */     this.jButton1.setText("Find");
/*  79: 78 */     this.jButton1.addActionListener(new ActionListener()
/*  80:    */     {
/*  81:    */       public void actionPerformed(ActionEvent evt)
/*  82:    */       {
/*  83: 80 */         LE8Search.this.jButton1ActionPerformed(evt);
/*  84:    */       }
/*  85: 82 */     });
/*  86: 83 */     gridBagConstraints = new GridBagConstraints();
/*  87: 84 */     gridBagConstraints.gridx = 1;
/*  88: 85 */     gridBagConstraints.gridy = 1;
/*  89: 86 */     gridBagConstraints.fill = 1;
/*  90: 87 */     gridBagConstraints.anchor = 18;
/*  91: 88 */     gridBagConstraints.insets = new Insets(4, 80, 4, 4);
/*  92: 89 */     this.jPanel1.add(this.jButton1, gridBagConstraints);
/*  93:    */     
/*  94: 91 */     this.jLabel2.setHorizontalAlignment(11);
/*  95: 92 */     gridBagConstraints = new GridBagConstraints();
/*  96: 93 */     gridBagConstraints.gridx = 0;
/*  97: 94 */     gridBagConstraints.gridy = 1;
/*  98: 95 */     gridBagConstraints.fill = 1;
/*  99: 96 */     gridBagConstraints.insets = new Insets(4, 4, 4, 4);
/* 100: 97 */     this.jPanel1.add(this.jLabel2, gridBagConstraints);
/* 101:    */     
/* 102: 99 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 103:100 */     getContentPane().setLayout(layout);
/* 104:101 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jPanel1, -2, -1, -2).addContainerGap(-1, 32767)));
/* 105:    */     
/* 106:    */ 
/* 107:    */ 
/* 108:    */ 
/* 109:    */ 
/* 110:107 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jPanel1, -2, 84, -2).addContainerGap(-1, 32767)));
/* 111:    */     
/* 112:    */ 
/* 113:    */ 
/* 114:    */ 
/* 115:    */ 
/* 116:    */ 
/* 117:114 */     pack();
/* 118:    */   }
/* 119:    */   
/* 120:    */   private void jButton1ActionPerformed(ActionEvent evt)
/* 121:    */   {
/* 122:118 */     if (this.jTextField1.getText().length() == 0)
/* 123:    */     {
/* 124:119 */       this.pos = 0;
/* 125:120 */       message("No pattern!");
/* 126:    */     }
/* 127:    */     else
/* 128:    */     {
/* 129:122 */       search(this.textComp, this.jTextField1.getText());
/* 130:    */     }
/* 131:    */   }
/* 132:    */   
/* 133:    */   class MyHighlightPainter
/* 134:    */     extends DefaultHighlighter.DefaultHighlightPainter
/* 135:    */   {
/* 136:    */     public MyHighlightPainter(Color color)
/* 137:    */     {
/* 138:129 */       super();
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:134 */   Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(UIManager.getColor("Table.selectionBackground"));
/* 143:    */   private JButton jButton1;
/* 144:    */   private JLabel jLabel1;
/* 145:    */   private JLabel jLabel2;
/* 146:    */   private JPanel jPanel1;
/* 147:    */   private JTextField jTextField1;
/* 148:    */   
/* 149:    */   private void search(JTextComponent textComp, String pattern)
/* 150:    */   {
/* 151:138 */     removeHighlights(textComp);
/* 152:    */     
/* 153:140 */     Highlighter hilite = textComp.getHighlighter();
/* 154:141 */     Document doc = textComp.getDocument();
/* 155:142 */     String text = null;
/* 156:    */     try
/* 157:    */     {
/* 158:144 */       text = doc.getText(0, doc.getLength());
/* 159:    */     }
/* 160:    */     catch (BadLocationException ex)
/* 161:    */     {
/* 162:146 */       System.out.println(ex);
/* 163:    */     }
/* 164:148 */     int index = text.toLowerCase().indexOf(pattern.toLowerCase(), this.pos);
/* 165:149 */     if (index >= 0)
/* 166:    */     {
/* 167:    */       try
/* 168:    */       {
/* 169:151 */         this.pos = (index + pattern.length());
/* 170:152 */         hilite.addHighlight(index, this.pos, this.myHighlightPainter);
/* 171:153 */         textComp.setCaretPosition(index);
/* 172:154 */         textComp.moveCaretPosition(this.pos);
/* 173:    */         
/* 174:156 */         message("Found at " + textComp.getCaretPosition());
/* 175:    */       }
/* 176:    */       catch (BadLocationException e)
/* 177:    */       {
/* 178:158 */         System.out.println(e);
/* 179:    */       }
/* 180:    */     }
/* 181:    */     else
/* 182:    */     {
/* 183:161 */       this.pos = 0;
/* 184:162 */       textComp.setCaretPosition(0);
/* 185:163 */       removeHighlights(textComp);
/* 186:164 */       message("Not found");
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   private void removeHighlights(JTextComponent textComp)
/* 191:    */   {
/* 192:171 */     Highlighter hilite = textComp.getHighlighter();
/* 193:172 */     Highlighter.Highlight[] hilites = hilite.getHighlights();
/* 194:174 */     for (int i = 0; i < hilites.length; i++) {
/* 195:175 */       if ((hilites[i].getPainter() instanceof MyHighlightPainter)) {
/* 196:176 */         hilite.removeHighlight(hilites[i]);
/* 197:    */       }
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:    */   void message(String msg)
/* 202:    */   {
/* 203:182 */     this.jLabel2.setText(msg);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void setVisible(boolean vis)
/* 207:    */   {
/* 208:187 */     super.setVisible(vis);
/* 209:189 */     if (vis) {
/* 210:190 */       this.jTextField1.setText("");
/* 211:    */     } else {
/* 212:192 */       removeHighlights(this.textComp);
/* 213:    */     }
/* 214:    */   }
/* 215:    */   
/* 216:203 */   private int pos = 0;
/* 217:    */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Devices.LE8Search
 * JD-Core Version:    0.7.0.1
 */