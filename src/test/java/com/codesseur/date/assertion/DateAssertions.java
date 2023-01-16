package com.codesseur.date.assertion;

import com.codesseur.date.RecurrentDate;

public class DateAssertions {

  public static RecurrentDateAssert assertThat(RecurrentDate recurrentDate) {
    return new RecurrentDateAssert(recurrentDate);
  }

}