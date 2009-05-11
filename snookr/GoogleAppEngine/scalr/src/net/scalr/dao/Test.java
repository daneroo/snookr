/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.scalr.dao;

import net.scalr.model.CloudMap;

/**
 *
 * @author daniel
 */
public class Test {

    public void doit() {
        CloudMapDAO dao = new CloudMapDAO();
        showAll();
        System.out.println("Creating aname");
        CloudMap clm = new CloudMap("aname", "SomeValue".getBytes());
        dao.createOrUpdate(clm);
        System.out.println("Saved aname");
        showAll();

        CloudMap check = dao.get("aname");
        System.out.println("Fecth aname: "+check.getName()+" content: "+check.getContent());
        showAll();

        CloudMap clmupd = dao.get("aname");
        clmupd.setContent("SomeUpdatedValue".getBytes());
        dao.createOrUpdate(clmupd);
        System.out.println("Updated aname");
        showAll();

        dao.delete("aname");
        showAll();

    }

    private void showAll() {
        CloudMapDAO dao = new CloudMapDAO();
        dao.print(dao.getAll());
    }
}
