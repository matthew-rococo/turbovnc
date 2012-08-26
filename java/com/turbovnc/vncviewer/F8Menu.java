/* Copyright (C) 2002-2005 RealVNC Ltd.  All Rights Reserved.
 * Copyright (C) 2011 Brian P. Hinz
 * Copyright (C) 2012 D. R. Commander.  All Rights Reserved.
 * 
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */

package com.turbovnc.vncviewer;

import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

import com.turbovnc.rfb.*;

public class F8Menu extends JPopupMenu implements ActionListener {
  public F8Menu(CConn cc_) {
    super("VNC Menu");
    setLightWeightPopupEnabled(false);
    cc = cc_;
    restore    = addMenuItem("Restore");
    move       = addMenuItem("Move");
    move.setEnabled(false);
    size       = addMenuItem("Size");
    size.setEnabled(false);
    minimize   = addMenuItem("Minimize");
    maximize   = addMenuItem("Maximize");
    addSeparator();
    exit       = addMenuItem("Close viewer", KeyEvent.VK_C);
    addSeparator();
    options    = addMenuItem("Options...", KeyEvent.VK_O);
    info       = addMenuItem("Connection Info...", KeyEvent.VK_I);
    addSeparator();
    refresh    = addMenuItem("Request screen refresh", KeyEvent.VK_R);
    addSeparator();
    fullScreen = new JCheckBoxMenuItem("Full screen");
    fullScreen.setMnemonic(KeyEvent.VK_F);
    fullScreen.setSelected(cc.fullScreen);
    fullScreen.addActionListener(this);
    add(fullScreen);
    addSeparator();
    f8 = addMenuItem("Send " + 
      KeyEvent.getKeyText(MenuKey.getMenuKeyCode()), MenuKey.getMenuKeyCode());
    ctrlAltDel = addMenuItem("Send Ctrl-Alt-Del");
    ctrlEsc = addMenuItem("Send Ctrl-Esc");
    addSeparator();
    clipboard  = addMenuItem("Clipboard...");
    addSeparator();
    newConn    = addMenuItem("New connection...", KeyEvent.VK_N);
    addSeparator();
    about      = addMenuItem("About TurboVNC Viewer...", KeyEvent.VK_A);
    addSeparator();
    dismiss    = addMenuItem("Dismiss menu");
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  JMenuItem addMenuItem(String str, int mnemonic) {
    JMenuItem item = new JMenuItem(str, mnemonic);
    item.addActionListener(this);
    add(item);
    return item;
  }

  JMenuItem addMenuItem(String str) {
    JMenuItem item = new JMenuItem(str);
    item.addActionListener(this);
    add(item);
    return item;
  }

  boolean actionMatch(ActionEvent ev, JMenuItem item) {
    return ev.getActionCommand().equals(item.getActionCommand());
  }

  public void actionPerformed(ActionEvent ev) {
    if (actionMatch(ev, exit)) {
      cc.close();
    } else if (actionMatch(ev, fullScreen)) {
      cc.toggleFullScreen();
    } else if (actionMatch(ev, restore)) {
      if (cc.fullScreen) cc.toggleFullScreen();
      cc.viewport.setExtendedState(JFrame.NORMAL);
    } else if (actionMatch(ev, minimize)) {
      if (cc.fullScreen) cc.toggleFullScreen();
      cc.viewport.setExtendedState(JFrame.ICONIFIED);
    } else if (actionMatch(ev, maximize)) {
      if (cc.fullScreen) cc.toggleFullScreen();
      cc.viewport.setExtendedState(JFrame.MAXIMIZED_BOTH);
    } else if (actionMatch(ev, clipboard)) {
      cc.clipboardDialog.showDialog(cc.viewport);
    } else if (actionMatch(ev, f8)) {
      cc.writeKeyEvent(MenuKey.getMenuKeySym(), true);
      cc.writeKeyEvent(MenuKey.getMenuKeySym(), false);
    } else if (actionMatch(ev, ctrlAltDel)) {
      cc.writeKeyEvent(Keysyms.Control_L, true);
      cc.writeKeyEvent(Keysyms.Alt_L, true);
      cc.writeKeyEvent(Keysyms.Delete, true);
      cc.writeKeyEvent(Keysyms.Delete, false);
      cc.writeKeyEvent(Keysyms.Alt_L, false);
      cc.writeKeyEvent(Keysyms.Control_L, false);
    } else if (actionMatch(ev, ctrlEsc)) {
      cc.writeKeyEvent(Keysyms.Control_L, true);
      cc.writeKeyEvent(Keysyms.Escape, true);
      cc.writeKeyEvent(Keysyms.Escape, false);
      cc.writeKeyEvent(Keysyms.Control_L, false);
    } else if (actionMatch(ev, refresh)) {
      cc.refresh();
    } else if (actionMatch(ev, newConn)) {
      VncViewer.newViewer(cc.viewer);
    } else if (actionMatch(ev, options)) {
      cc.options.showDialog(cc.viewport);
    } else if (actionMatch(ev, info)) {
      cc.showInfo();
    } else if (actionMatch(ev, about)) {
      cc.showAbout();
    } else if (actionMatch(ev, dismiss)) {
      firePopupMenuCanceled();
    }
  }

  CConn cc;
  JMenuItem restore, move, size, minimize, maximize;
  JMenuItem exit, clipboard, ctrlAltDel, ctrlEsc, refresh;
  JMenuItem newConn, options, info, about, dismiss;
  static JMenuItem f8;
  JCheckBoxMenuItem fullScreen;
  static LogWriter vlog = new LogWriter("F8Menu");
}
