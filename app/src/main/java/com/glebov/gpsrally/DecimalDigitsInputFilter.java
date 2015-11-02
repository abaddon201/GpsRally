package com.glebov.gpsrally;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter extends DigitsKeyListener {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero) + "}+((\\.[0-9]{0," + (digitsAfterZero) + "})?)||(\\.)?");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String s;
        if ((start == end) && (end == 0)) {
            s = dest.subSequence(0, dstart).toString() + dest.subSequence(dend, dest.length()).toString();
        } else {
            s = dest.subSequence(0, dstart).toString() + source.subSequence(start, end) + dest.subSequence(dend, dest.length()).toString();
        }
        Matcher matcher = mPattern.matcher(s);
        if (!matcher.matches())
            return "";
        return null;
    }

}
