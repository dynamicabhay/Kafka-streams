package com.kafkastreams.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsEvent {
    public int userId;
    public double amountInInr;
}


// "{ "useId" : 1, "amountInInr" : 123.45 }"
