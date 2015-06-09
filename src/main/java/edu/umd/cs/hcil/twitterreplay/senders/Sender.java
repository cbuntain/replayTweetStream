/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umd.cs.hcil.twitterreplay.senders;

import java.util.List;

/**
 *
 * @author cbuntain
 */
public interface Sender {
    public void send(List<String> messages);
}
