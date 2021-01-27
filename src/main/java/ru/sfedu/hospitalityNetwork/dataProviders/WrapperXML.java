/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sfedu.hospitalityNetwork.dataProviders;

import java.util.List;

import lombok.Data;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name = "List")
public class WrapperXML<T> {
  @ElementList(inline = true, required = false)
  public List<T> list;

  public WrapperXML() { }

  public WrapperXML(List<T> list){
    this.list = list;
  }

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }
}
