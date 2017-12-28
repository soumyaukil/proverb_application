/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proverbs.models;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author soumukil
 */

@Builder
@Getter
@Setter
public class ProverbViewModel {
    private String id;
    private String text;
    private List<String> tags;
}
