Feature: Total Funds Required Calculation - Continuation Tier 2 applicant with and without dependants (single current account)

    Main applicants Required Maintenance - £945
    Dependants Required Maintenance - £630 (per dependant)
    Dependants can apply without the main dependant

    Required Maintenance threshold calculation to pass this feature file:

    Main applicant required maintenance + (dependants maintenance maintenance * number of dependants)

    Worked examples:

    Tier 2 applicant without dependent (£945 x 1) + (£630 x 0) = £956
    Tier 2 applicant with dependant (£945 x 1) + (£630 x 1) = £1575
    Tier 2 applicant with dependant (£945 x 1) + (£630 x 2) = £2205
    Tier 2 dependant only (£945 x 0) + (£630 x 1) = £630

    ###### Tier 2 ########

    Scenario: Leo is Tier 2 applicant. Leo's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Main applicant | Yes |
            | Dependants     | 0   |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200    |
            | Threshold   | 945.00 |

    Scenario: Fran is Tier 2 applicant. Fran's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Main applicant | Yes |
            | Dependants     | 1   |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 1575.00 |

    Scenario: Karen is Tier 2 applicant. Karen's maintenance threshold amount calculated.

        Given A Service is consuming the FSPS Calculator API
        When the FSPS Calculator API is invoked with the following
            | Main applicant | Yes |
            | Dependants     | 2   |
        Then The Financial Status API provides the following results:
            | HTTP Status | 200     |
            | Threshold   | 2205.00 |
