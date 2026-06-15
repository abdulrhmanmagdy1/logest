package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountantDashboardStats {
    private Double liquidity;
    private Double outstanding_debts;
    private Double monthly_expenses;
    private Double net_profit;
    private List<ChartData> revenue_history;
    private List<ChartData> expense_distribution;
    private List<ChartData> profit_history;
}
