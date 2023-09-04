package com.destiny.utils;

public class CpfValidator {

  public static boolean isValid(String cpf) {
    if (cpf == null || cpf.length() != 11 || cpf.chars().allMatch(n -> n == cpf.charAt(0))) {
      return false;
    }

    return calculateDigit(cpf.substring(0, 9)).equals(cpf.substring(9, 11));
  }

  private static String calculateDigit(String str) {
    char[] numbers = str.toCharArray();

    int firstDigit = calculateWeightedSum(numbers, 10) % 11;
    firstDigit = firstDigit < 2 ? 0 : 11 - firstDigit;

    int secondDigit = calculateWeightedSum(numbers, 11) % 11;
    secondDigit = secondDigit < 2 ? 0 : 11 - secondDigit;

    return String.valueOf(firstDigit) + secondDigit;
  }

  private static int calculateWeightedSum(char[] numbers, int weight) {
    int sum = 0;

    for (int i = 0; i < numbers.length; i++, weight--) {
      sum += (numbers[i] - '0') * weight;
    }

    return sum;
  }

}
