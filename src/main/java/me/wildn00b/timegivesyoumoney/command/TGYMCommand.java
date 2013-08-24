/*
 * TimeGivesYouMoney - Gives players money every time interval
 * Copyright (C) 2013 Dan Printzell
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package me.wildn00b.timegivesyoumoney.command;

import java.util.ArrayList;

import me.wildn00b.timegivesyoumoney.TimeGivesYouMoney;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TGYMCommand implements CommandExecutor {
  private final TimeGivesYouMoney tgym;

  public TGYMCommand(TimeGivesYouMoney tgym) {
    this.tgym = tgym;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label,
      String[] args) {
    double result = 0;

    try {
      if (args.length == 1) {
        if (args[0].equalsIgnoreCase("cashout")
            && p(sender, "tgym.cashout.self")) {
          result = tgym.Bank.CashOut(((Player) sender).getName());
          if (result == -1)
            send(sender, tgym.Lang._("Command.Cashout.Failed"));
          else
            send(
                sender,
                tgym.Lang._("Command.Cashout.Success.Self").replaceAll(
                    "%MONEY%",
                    result + tgym.Vault.GetEconomy().currencyNamePlural()));
        } else
          ShowHelp(sender, label, 1);
      } else if (args.length == 2) {
        if (args[0].equalsIgnoreCase("help"))
          ShowHelp(sender, label, Integer.parseInt(args[1]));
        else if (args[0].equalsIgnoreCase("cashout")
            && p(sender, "tgym.cashout.other")) {
          result = tgym.Bank.CashOut(args[1]);
          if (result == -1)
            send(sender, tgym.Lang._("Command.Cashout.Failed"));
          else
            send(
                sender,
                tgym.Lang
                    ._("Command.Cashout.Success.Other")
                    .replaceAll(
                        "%MONEY%",
                        result + " "
                            + tgym.Vault.GetEconomy().currencyNamePlural())
                    .replaceAll("%PLAYER%", args[1]));
        } else
          ShowHelp(sender, label, 1);
      } else if (args.length == 3) {
        if (args[0].equalsIgnoreCase("add")) {
          tgym.Bank.Add(args[1], Double.parseDouble(args[2]));
          send(
              sender,
              tgym.Lang
                  ._("Command.Add.Other.Success")
                  .replaceAll(
                      "%MONEY%",
                      args[2] + " "
                          + tgym.Vault.GetEconomy().currencyNamePlural())
                  .replaceAll("%PLAYER%", args[1]));
        } else if (args[0].equalsIgnoreCase("remove")) {
          tgym.Bank.Add(args[1], Double.parseDouble(args[2]));
          send(
              sender,
              tgym.Lang
                  ._("Command.Remove.Other.Success")
                  .replaceAll(
                      "%MONEY%",
                      args[2] + " "
                          + tgym.Vault.GetEconomy().currencyNamePlural())
                  .replaceAll("%PLAYER%", args[1]));
        } else
          ShowHelp(sender, label, 1);
      } else
        ShowHelp(sender, label, 1);
    } catch (final ArrayIndexOutOfBoundsException e) {
      ShowHelp(sender, label, 1);
    } catch (final Exception e) {
      e.printStackTrace();
      send(sender, tgym.Lang._("Command.Exception"));
    }

    return true;
  }

  private boolean p(CommandSender sender, String permissions) {
    return p(sender, permissions, true);
  }

  private boolean p(CommandSender sender, String permissions,
      boolean consoleDefault) {
    if (sender instanceof Player)
      return tgym.Vault.HasPermissions((Player) sender, permissions);
    else
      return consoleDefault;
  }

  private void send(CommandSender sender, String msg) {
    sender.sendMessage(ChatColor.YELLOW + "[TimeGivesYouMoney] "
        + ChatColor.GOLD + msg);
  }

  private void ShowHelp(CommandSender sender, String label, int page) {
    final ArrayList<String> cmds = new ArrayList<String>();

    cmds.add("help "
        + tgym.Lang._("Command.Help.Help").replaceFirst("- ",
            ChatColor.DARK_AQUA + "-" + ChatColor.GOLD + " "));

    if (p(sender, "tgym.cashout.self", false))
      cmds.add("cashout "
          + tgym.Lang._("Command.Help.Cashout.Self").replaceFirst("- ",
              ChatColor.DARK_AQUA + "-" + ChatColor.GOLD + " "));

    if (p(sender, "tgym.cashout.other"))
      cmds.add("cashout "
          + tgym.Lang._("Command.Help.Cashout.Other").replaceFirst("- ",
              ChatColor.DARK_AQUA + "-" + ChatColor.GOLD + " "));

    if (p(sender, "tgym.add"))
      cmds.add("add "
          + tgym.Lang._("Command.Help.Add").replaceFirst("- ",
              ChatColor.DARK_AQUA + "-" + ChatColor.GOLD + " "));

    if (p(sender, "tgym.remove"))
      cmds.add("remove "
          + tgym.Lang._("Command.Help.Remove").replaceFirst("- ",
              ChatColor.DARK_AQUA + "-" + ChatColor.GOLD + " "));

    final int maxpage = 1 + cmds.size() / 6;
    if (page < 1)
      page = 1;
    else if (page > maxpage)
      page = maxpage;
    sender.sendMessage(""
        + ChatColor.RED
        + ChatColor.BOLD
        + tgym.Lang
            ._("Command.Title")
            .replaceAll("%VERSION%", tgym.Version)
            .replaceAll("%PAGE%", "" + ChatColor.RED + page + ChatColor.AQUA)
            .replaceAll("%MAXPAGE%",
                "" + ChatColor.BLUE + maxpage + ChatColor.GOLD)
            .replaceAll("%AUTHOR%", ChatColor.YELLOW + "WildN00b"));
    try {
      for (int i = (page - 1) * 6; i < ((page - 1) * 6) + 6; i++)
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " " + cmds.get(i));

    } catch (final Exception e) {
    }
  }

}