package com.ianarbuckle.dublinbushelper.search.selection.ui;

import com.ianarbuckle.dublinbushelper.models.stopinfo.Result;

import java.util.List;

/**
 * Created by Ian Arbuckle on 24/09/2017.
 *
 */

public interface SelectionView {
  void setResults(List<Result> results);
  void showError();
  void showProgress();
  void hideProgress();
}
