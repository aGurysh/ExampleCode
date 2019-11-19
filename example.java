 //method that sumbits results to DB and sends emails
    private void submitFormResults() throws SQLException {

        Connection connection = DriverManager.getConnection(CONURL);

        if (connection == null) {
            throw new SQLException("Unable to connect to the DataSource");

        }

        try {

            PreparedStatement setData = connection.prepareStatement("INSERT INTO ****** "
                    + "( Blurb, RecDate, Recognizer,Recognizee,Dept,Fundamental,Fundamental2, Fundamental3,Fundamental4,Fundamental5 )"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?)");
            for (int i = 6; i <= 10; i++) {
                setData.setString(i, null);
            }

            setData.setString(1, getBlurb());
            setData.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            setData.setString(3, eid);
            //find the employee object for the selected employee. 

            setData.setString(4, selectedEmployeeObject.getId());
            setData.setString(5, departmentMap.get(selectedEmployeeObject.getDepartment()));

            //add all selected fundamentals to to preparedstatement
            for (int i = 0; i < selectedFundamentals.size(); i++) {
                setData.setInt(6 + i, fundamentals.indexOf(selectedFundamentals.get(i)) + 1);
            }

            setData.executeUpdate();

        } finally {
            connection.close();

            //add the employees manager to the toList
            for (Employee emp : employeeList) {
                String name = emp.toString();

                int firstSpace = name.indexOf(" ");
                int secondSpace = name.indexOf(" ", firstSpace + 1);

                if (secondSpace != -1) {
                    name = name.substring(0, secondSpace);
                }

                if (name.equals(selectedEmployeeObject.getManager())) {
                    toList.add(emp.getEmail());
                }

            }

            JavaEmail mailer = new JavaEmail();

            try {
                //create an email, employee to be recognized, list of people to recieve the email, fundamental that they demonstrated
                String fundamentalString = "";
                fundamentalString += selectedFundamentals.get(0);
                for (int i = 1; i < selectedFundamentals.size(); i++) {
                    fundamentalString += ", " + selectedFundamentals.get(i);
                }
                mailer.createNotifyEmail(selectedEmployeeObject.getReadableName(), recognizerObject.getReadableName(), toList, fundamentalString, blurb);
                mailer.sendNotifyEmail();
                mailer.createConfirmEmail(confirmAddress, selectedEmployeeObject.getReadableName(), fundamentalString, blurb);
                mailer.sendConfirmEmail();
            } catch (MessagingException ex) {
                Logger.getLogger(DropdownView.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }