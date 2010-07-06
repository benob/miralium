/* Copyright (C) (2009) (Benoit Favre) <benoit.favre@gmail.com>

This program is free software; you can redistribute it and/or 
modify it under the terms of the GNU Lesser General Public License 
as published by the Free Software Foundation; either 
version 2 of the License, or (at your option) any later 
version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. */

/* 
 * 2009-12-27 added support for import/export of CRF++ compatible text models
 */

package edu.lium.mira;

import java.util.*;
import java.io.*;

class AsrSemanticModel extends Mira {
    public double getNgramScore(String text) {
        return getNgramScore(text.split(" "));
    }
    public double getNgramScore(String text[]) {
        Vector<String[]> parts = new Vector<String[]>();
        for(int i = 0; i < text.length; i++) {
            String tokens[] = text[i].split("@");
            if(tokens[0].startsWith("[")) continue; // skip [carillon] but keep <s> </s>...
            if(tokens.length == 2) {
                parts.add(tokens);
            } else {
                parts.add(new String[]{tokens[0], "o"});
            }
        }
        Example example = encodeFeatures(parts, false, false); // no new features, no features for begining/end of string
        example.score = 0;
        for(int position = 0; position < example.labels.length; position++) {
            example.score += computeScore(example, position, example.labels[position]);
            if(position > 0) example.score += computeScore(example, position, example.labels[position], example.labels[position - 1]);
        }
        return example.score;
    }
    public AsrSemanticModel(String modelName) {
        try {
            if(modelName.endsWith(".txt")) loadTextModel(modelName);
            else loadModel(modelName);
        } catch(Exception e) {
            System.err.println("ERROR: could not load MIRA model \"" + modelName + "\"");
            e.printStackTrace();
        }
    }
}
