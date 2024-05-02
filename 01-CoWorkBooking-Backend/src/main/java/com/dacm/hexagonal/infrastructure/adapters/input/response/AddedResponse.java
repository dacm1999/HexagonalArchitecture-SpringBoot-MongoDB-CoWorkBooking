package com.dacm.hexagonal.infrastructure.adapters.input.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AddedResponse {
    private boolean success;
    private int total;
    private int num_added;
    private int num_failed;
    private ArrayList added;
    private ArrayList failed;


    public AddedResponse(boolean success, int total, int num_added, int num_failed, ArrayList added, ArrayList failed) {
        this.success = success;
        this.total = total;
        this.num_added = num_added;
        this.num_failed = num_failed;
        this.added = added;
        this.failed = failed;
    }

}