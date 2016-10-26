$ cc/debug/optim=noinline pdp8_cpu
$ cc/debug/optim=noinline pdp8_sys
$ cc/debug/optim=noinline pdp8_pt
$ cc/debug/optim=noinline pdp8_tt
$ cc/debug/optim=noinline pdp8_lp
$ cc/debug/optim=noinline pdp8_clk
$ cc/debug/optim=noinline pdp8_rk
$ cc/debug/optim=noinline pdp8_rx
$ cc/debug/optim=noinline pdp8_rf
$ cc/debug/optim=noinline scp,scp_tty
$ link/debug/exec=pdp8 pdp8_cpu,pdp8_sys,pdp8_pt,pdp8_tt,pdp8_lp,pdp8_clk,-
  pdp8_rk,pdp8_rx,pdp8_rf,scp,scp_tty,sys$library:vaxcrtl/lib
