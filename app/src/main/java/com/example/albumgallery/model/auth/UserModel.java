    package com.example.albumgallery.model.auth;

    import com.example.albumgallery.model.Model;

    public class UserModel implements Model {
        // User class for authentication

        private String username;
        private String password;
        private String email;
        private String phone;
        private String address;

        public UserModel() {
            // Constructor không đối số
        }
        public UserModel(String username, String password, String email, String phone, String address) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.address = address;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setAddress(String address) {
            this.address = address;
        }


        public String insert() {
            return "INSERT INTO users (username, password, email, phone, address) VALUES ('" + username + "', '" + password + "', '" + email + "', '" + phone + "', '" + address + "')";
        }

        public void delete() {
            // Delete the user
        }

        public void update() {
            // Update the user
        }

        @Override
        public void select() {

        }
    }
