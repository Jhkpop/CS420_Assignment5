CREATE TABLE Investor(
InvestorID int primary key NOT NULL,
FirstName varchar(50),
LastName varchar(50),
Email varchar(50),
Phone varchar(13)
);
CREATE TABLE Company(
CompanyID int primary key NOT NULL,
Industry varchar(50) NOT NULL,
Headquarters varchar(50),
FoundedYear year
);

CREATE TABLE BrokerageAccount(
AccountID int primary key NOT NULL,
AccountType varchar(50) NOT NULL,
Balance decimal(10, 2),
InvestorID int NOT NULL,
CONSTRAINT Investor_ID FOREIGN KEY (InvestorID) REFERENCES Investor(InvestorID)
);

CREATE TABLE Stock(
StockID int primary key NOT NULL,
TickerSymbol varchar(50),
ExchangeName varchar(50),
CurrentPrice decimal(10, 2),
CompanyID int NOT NULL,
CONSTRAINT Company_ID FOREIGN KEY (Company) REFERENCES Company(CompanyID)
);


CREATE TABLE TradeTransaction(
TransactionID int primary key NOT NULL,
TradeDate date NOT NULL,
TradeType varchar(50),
Quantity int,
PricePerShare decimal(10, 2),
AccountID int NOT NULL,
CONSTRAINT Account_ID FOREIGN KEY (AccountID) REFERENCES BrokerageAccount(AccountID),
StockID int NOT NULL,
CONSTRAINT Stock_ID FOREIGN KEY (Stock) REFERENCES Stock(StockID)
);
