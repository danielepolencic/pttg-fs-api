Feature: Outgoing API request to the Barclays Consent API and handling the incoming responses

    Sort code – 6 digits – sort code of the applicants current account
    Account number – 8 digits – account number of the applicants current account an Account number will be padded with leading zeroes to ensure it to be 8 digits
    Date of Birth of the applicant in DD-MMM-YYYY format
    User ID – the unique identifier of the user
    From Date - start date of data request in DD-MMM-YYYY format
    To Date - end date of data request in DD-MMM-YYYY format

    Background:
        Given A Service is consuming the FSPS Calculator API
        And the default details are
            | Sort code      |             |
            | Account number |             |
            | Date of birth  | DD-MMM-YYYY |
            | User ID        |             |
            | FromDate       |             |
            | ToDate         |             |

    Scenario: Outgoing Balances API request and consent has not been granted

        Given A Service is consuming the Barclays Balances API
        When the Balances API is invoked
        Then The Barclays Consent API provides the following error response:
            | Account-Holder consent unavailable |

    Scenario: Outgoing Balances API request and consent has expired (e.g. greater than 24 hopurs)

        Given A Service is consuming the Barclays Balances API
        When the Balances API is invoked
        Then The Barclays Consent API provides the following error response:
            | Account-Holder consent unavailable |