import 'package:flutter/material.dart';

class StatisticCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;
  final String? change;
  final String? changeType;

  const StatisticCard({
    Key? key,
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
    this.change,
    this.changeType,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFF1A1F2E),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: Colors.white.withOpacity(0.1),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Container(
                width: 32,
                height: 32,
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(
                  icon,
                  color: color,
                  size: 18,
                ),
              ),
              if (change != null) ...[
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: changeType == 'positive' 
                        ? const Color(0xFF10B981).withOpacity(0.2)
                        : const Color(0xFFEF4444).withOpacity(0.2),
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(
                        changeType == 'positive' 
                            ? Icons.arrow_upward
                            : Icons.arrow_downward,
                        color: changeType == 'positive' 
                            ? const Color(0xFF10B981)
                            : const Color(0xFFEF4444),
                        size: 12,
                      ),
                      const SizedBox(width: 2),
                      Text(
                        change!,
                        style: TextStyle(
                          color: changeType == 'positive' 
                              ? const Color(0xFF10B981)
                              : const Color(0xFFEF4444),
                          fontSize: 10,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ],
          ),
          const SizedBox(height: 12),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            title,
            style: TextStyle(
              color: Colors.white.withOpacity(0.6),
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }
}
