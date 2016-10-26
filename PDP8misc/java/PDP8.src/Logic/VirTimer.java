/*  1:   */ package Logic;
/*  2:   */ 
/*  3:   */ public class VirTimer
/*  4:   */ {
/*  5:   */   int initialDelay;
/*  6:   */   int delay;
/*  7:   */   boolean repeats;
/*  8:   */   long expirationTime;
/*  9:   */   boolean running;
/* 10:   */   VirQueue virqueue;
/* 11:   */   VirListener virlistener;
/* 12:   */   VirTimer nextTimer;
/* 13:   */   
/* 14:   */   public int getDelay()
/* 15:   */   {
/* 16:21 */     return this.delay;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public int getInitialDelay()
/* 20:   */   {
/* 21:25 */     return this.initialDelay;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void post()
/* 25:   */   {
/* 26:29 */     if (this.virlistener != null) {
/* 27:30 */       this.virlistener.actionPerformed();
/* 28:   */     }
/* 29:   */   }
/* 30:   */   
/* 31:   */   public void restart()
/* 32:   */   {
/* 33:35 */     stop();
/* 34:36 */     start();
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void start()
/* 38:   */   {
/* 39:40 */     this.virqueue.addTimer(this, getInitialDelay());
/* 40:   */   }
/* 41:   */   
/* 42:   */   public void stop()
/* 43:   */   {
/* 44:44 */     this.virqueue.removeTimer(this);
/* 45:   */   }
/* 46:   */   
/* 47:   */   public boolean isRepeats()
/* 48:   */   {
/* 49:48 */     return this.repeats;
/* 50:   */   }
/* 51:   */   
/* 52:   */   public boolean isRunning()
/* 53:   */   {
/* 54:52 */     return this.virqueue.containsTimer(this);
/* 55:   */   }
/* 56:   */   
/* 57:   */   public void setDelay(int i)
/* 58:   */   {
/* 59:56 */     if (i < 0) {
/* 60:57 */       throw new IllegalArgumentException("Invalid delay: " + i);
/* 61:   */     }
/* 62:59 */     this.delay = i;
/* 63:   */   }
/* 64:   */   
/* 65:   */   public void setInitialDelay(int i)
/* 66:   */   {
/* 67:65 */     if (i < 0) {
/* 68:66 */       throw new IllegalArgumentException("Invalid initial delay: " + i);
/* 69:   */     }
/* 70:68 */     this.initialDelay = i;
/* 71:   */   }
/* 72:   */   
/* 73:   */   public void setRepeats(boolean flag)
/* 74:   */   {
/* 75:75 */     this.repeats = flag;
/* 76:   */   }
/* 77:   */   
/* 78:   */   public VirTimer(VirQueue virqueue, int i, VirListener virlistener)
/* 79:   */   {
/* 80:79 */     this.virqueue = virqueue;
/* 81:80 */     this.repeats = true;
/* 82:81 */     this.delay = i;
/* 83:82 */     this.initialDelay = i;
/* 84:83 */     if (virlistener != null) {
/* 85:84 */       this.virlistener = virlistener;
/* 86:   */     }
/* 87:   */   }
/* 88:   */ }


/* Location:           C:\Users\Paul\Downloads\PDP8.jar
 * Qualified Name:     Logic.VirTimer
 * JD-Core Version:    0.7.0.1
 */