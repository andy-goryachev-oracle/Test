package goryachev.research;

import java.util.function.Function;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Cell;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.FocusModel;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.skin.LabeledSkinBase;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

// ***** ISSUES *****
// - TabPane: Tab Changed is fired before focus
// - Virtualized Control: Focus and Selection change is fired before focus
// --- Tree/TableView: Selection change is fired BEFORE focus change
// - ToolBar: Overflow Button does not request Focus
// - Non focus traversable button does not work
//
// ***** OTHER *****
// MenuBar / Menu / MenuItem does not trigger Focus at all.
//
// ***** WORKS *****
// - Mnemonics work
// - TabPane Overflow Button DOES request Focus
//
// https://bugs.openjdk.org/browse/JDK-8089514
// https://github.com/openjdk/jfx/pull/1935#issuecomment-4048456415
public class FocusTestApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<Person> tableView = generateTable();
        TabPane content = generateContent();

        SplitPane splitPane = new SplitPane(new StackPane(tableView), new StackPane(content));
        splitPane.setPadding(new Insets(4));

        Scene scene = new Scene(new StackPane(splitPane), 1440, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static VBox createContent() {
        VBox content = new VBox(4);
        content.setPadding(new Insets(4));
        return content;
    }

    private static Menu createMenu(String Menu1) {
        Menu menu = new Menu(Menu1, null, createMenuItem("MenuItem1"), createMenuItem("MenuItem2"));
        menu.setOnShowing(_ -> System.out.println("Showing Menu: " + menu));
        return menu;
    }

    private static MenuItem createMenuItem(String text) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(_ -> System.out.println("ACTION ON: " + menuItem));
        return menuItem;
    }

    private static Person createPerson(int i) {
        Person person = new Person();
        person.setFirstName("First" + i);
        person.setLastName("Last" + i);
        person.setAge(i);
        person.setEmail("Email" + i);
        return person;
    }

    private static void focusListener(Node node, FocusModel<?> focusModel) {
        focusModel.focusedItemProperty().addListener((_) -> {
            System.out.println("Focus changed for: " + node + " to: " + focusModel.getFocusedItem());   
        });
    }

    private static TabPane generateContent() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().add(new Tab("Simple Controls", wrap(simpleControls())));
        tabPane.getTabs().add(new Tab("Wrapper Controls", wrap(wrapperControls())));
        tabPane.getTabs().add(new Tab("Virtualized Controls", wrap(virtualizedControls())));
        tabPane.getTabs().add(new Tab("Special Controls", wrap(specialControls())));
        return tabPane;
    }

    private static TableView<Person> generateTable() {
        TableView<Person> tableView = new TableView<>();
        tableView.setEditable(true);
        for (int i = 0; i < 100; i++) {
            Person person = createPerson(i);
            tableView.getItems().add(person);
        }

        TableColumn<Person, String> firstName = new TableColumn<>("First Name");
        firstName.setCellFactory(TextFieldTableCell.forTableColumn());
        firstName.setOnEditCommit(cde -> {
            System.out.println("commit firstName");
            cde.getRowValue().setFirstName(cde.getNewValue());
        });
        firstName.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getFirstName()));
        tableView.getColumns().add(firstName);

        TableColumn<Person, String> lastName = new TableColumn<>("Last Name");
        lastName.setCellFactory(TextFieldTableCell.forTableColumn());
        lastName.setOnEditCommit(cde -> {
            System.out.println("commit lastName");
            cde.getRowValue().setLastName(cde.getNewValue());
        });
        lastName.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getLastName()));
        tableView.getColumns().add(lastName);

        TableColumn<Person, String> email = new TableColumn<>("Email");
        email.setOnEditCommit(cde -> {
            System.out.println("commit email");
            cde.getRowValue().setEmail(cde.getNewValue());
        });
        email.setCellFactory(TextFieldTableCell.forTableColumn());
        email.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getEmail()));
        tableView.getColumns().add(email);

        TableColumn<Person, Integer> age = new TableColumn<>("Age");
        age.setOnEditCommit(cde -> {
            System.out.println("commit age");
            cde.getRowValue().setAge(cde.getNewValue());
        });
        age.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        age.setCellValueFactory(cdf -> new SimpleObjectProperty<>(cdf.getValue().getAge()));
        tableView.getColumns().add(age);
        return tableView;
    }

    private static void listener(Node node) {
        node.focusedProperty().addListener(_ -> {
            if (node.isFocused()) {
                System.out.println("FOCUS GAIN ON: " + node);
            }
        });

        if (node instanceof ButtonBase buttonBase) {
            buttonBase.setOnAction(_ -> System.out.println("ACTION ON: " + node));
        }
    }

    private static StackPane placeholder() {
        return placeholderSize(64);
    }

    private static StackPane placeholderSize(int widthHeight) {
        StackPane placeholder = new StackPane();
        placeholder.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null),
            new BackgroundFill(Color.color(Math.random(), Math.random(), Math.random()), null, new Insets(1))));
        placeholder.setMinSize(widthHeight, widthHeight);
        return placeholder;
    }

    private static void selectionListener(Node node, SelectionModel<?> selectionModel) {
        selectionModel.selectedItemProperty().addListener(
            _ -> System.out.println("Model changed for: " + node + " to: " + selectionModel.getSelectedItem()));
    }

    private static Node simpleControls() {
        VBox content = createContent();

        TextField txf = new TextField();
        txf.setPromptText("TextField");
        listener(txf);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("PasswordField");
        listener(passwordField);

        Button btn = new Button("Button");
        listener(btn);

        CheckBox cb = new CheckBox("CheckBox");
        listener(cb);

        RadioButton rb = new RadioButton("RadioButton");
        listener(rb);

        ToggleButton tb = new ToggleButton("ToggleButton");
        listener(tb);

        Label label = new Label("Label");
        listener(label);

        TextArea ta = new TextArea();
        ta.setPrefRowCount(1);
        ta.setPromptText("TextArea");
        listener(ta);

        Spinner<Integer> sp = new Spinner<>();
        sp.setValueFactory(new IntegerSpinnerValueFactory(0, 10, 1));
        listener(sp);

        ComboBox<Person> cbx = new ComboBox<>();
        cbx.setPromptText("ComboBox");
        cbx.getItems().add(new Person());
        listener(cbx);

        DatePicker dp = new DatePicker();
        listener(dp);

        Hyperlink hl = new Hyperlink("Hyperlink");
        listener(hl);

        MenuButton mb = new MenuButton("MenuButton");
        mb.getItems().add(new MenuItem("MenuItem"));
        listener(mb);

        ChoiceBox<Person> chbx = new ChoiceBox<>();
        chbx.getItems().add(new Person());
        listener(chbx);

        ColorPicker cp = new ColorPicker();
        listener(cp);

        Separator sepH = new Separator();
        listener(sepH);

        Separator sepV = new Separator();
        sepV.setMinHeight(32);
        sepV.setOrientation(Orientation.VERTICAL);
        listener(sepV);

        ProgressIndicator pi = new ProgressIndicator();
        listener(pi);

        ProgressBar pb = new ProgressBar();
        pb.setProgress(-1);
        listener(pb);

        Slider slider = new Slider();
        listener(slider);

        Cell<String> cell = new Cell<>() {
            @Override
            protected Skin<?> createDefaultSkin() {
                return new LabeledSkinBase<>(this) {
                };
            }
        };
        cell.setText("Cell");
        listener(cell);

        MenuBar menuBar = new MenuBar();
        fillWith(menuBar.getMenus(), i -> createMenu("Menu" + i));
        listener(menuBar);

        ToolBar toolBar = new ToolBar();
        fillWith(toolBar.getItems(), i -> new Button("Button" + i));
        listener(toolBar);

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.setButtonMinWidth(16);
        fillWith(buttonBar.getButtons(), i -> new Button("B" + i));
        listener(buttonBar);

        ScrollBar sbH = new ScrollBar();
        listener(sbH);

        ScrollBar sbV = new ScrollBar();
        sbV.setOrientation(Orientation.VERTICAL);
        listener(sbV);

        content.getChildren()
            .addAll(txf, passwordField, btn, cb, rb, tb, label, ta, sp, cbx, dp, hl, mb, chbx, cp, sepH, sepV,
                pi, pb, slider, cell, menuBar, toolBar, buttonBar, sbH, sbV);

        return content;
    }

    private static Node specialControls() {
        VBox content = createContent();

        Button btnM = new Button("_Mnemonic");
        btnM.setMnemonicParsing(true);
        listener(btnM);

        Button btnF = new Button("Not focus traversable");
        btnF.setFocusTraversable(false);
        listener(btnF);

        CheckBox chbF = new CheckBox("Not focus traversable");
        chbF.setFocusTraversable(false);
        listener(chbF);

        TextField txf = new TextField();
        txf.setPromptText("Not focus traversable");
        txf.setFocusTraversable(false);
        listener(txf);

        DatePicker dp = new DatePicker();
        dp.valueProperty().addListener((_) -> System.out.println("DatePicker commit: " + dp.getValue()));
        dp.setFocusTraversable(false);
        listener(dp);

        content.getChildren().addAll(btnM, btnF, chbF, txf, dp);

        return content;
    }

    private static Node virtualizedControls() {
        VBox content = createContent();

        ObservableList<Person> items = FXCollections.observableArrayList();
        for (int i = 0; i < 5; i++) {
            Person person = createPerson(i);
            items.add(person);
        }

        TableView<Person> tableView = new TableView<>();
        TableColumn<Person, String> tableNameCol = new TableColumn<>("First Name");
        tableNameCol.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getFirstName()));
        tableView.getColumns().add(tableNameCol);
        tableView.setItems(items);
        listener(tableView);
        selectionListener(tableView, tableView.getSelectionModel());
        focusListener(tableView, tableView.getFocusModel());

        TreeTableView<Person> treeTableView = new TreeTableView<>(new TreeItem<>(null));
        treeTableView.getRoot().setExpanded(true);
        TreeTableColumn<Person, String> treeNameCol = new TreeTableColumn<>("First Name");
        treeNameCol.setCellValueFactory(cdf -> {
            if (cdf.getValue().getValue() == null) {
                return null;
            }
            return new SimpleStringProperty(cdf.getValue().getValue().getFirstName());
        });
        treeTableView.getColumns().add(treeNameCol);
        for (Person item: items) {
            treeTableView.getRoot().getChildren().add(new TreeItem<>(item));
        }
        listener(treeTableView);
        selectionListener(treeTableView, treeTableView.getSelectionModel());
        focusListener(treeTableView, treeTableView.getFocusModel());

        ListView<Person> listView = new ListView<>();
        listView.setItems(items);
        listener(listView);
        selectionListener(listView, listView.getSelectionModel());
        focusListener(listView, listView.getFocusModel());

        TreeView<Person> treeView = new TreeView<>(new TreeItem<>(null));
        treeView.getRoot().setExpanded(true);
        for (Person item: items) {
            treeView.getRoot().getChildren().add(new TreeItem<>(item));
        }
        listener(treeView);
        selectionListener(treeView, treeView.getSelectionModel());
        focusListener(treeView, treeView.getFocusModel());

        content.getChildren().addAll(tableView, treeTableView, listView, treeView);

        return content;
    }

    private static Node wrap(Node node) {
        return node;
    }

    private static Node wrapperControls() {
        VBox content = createContent();

        SplitPane splitPane = new SplitPane(placeholder(), placeholder());
        listener(splitPane);

        TitledPane tp = new TitledPane("TitledPane", placeholder());
        listener(tp);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        fillWith(tabPane.getTabs(), i -> new Tab("Tab" + i, placeholder()));
        listener(tabPane);
        selectionListener(tabPane, tabPane.getSelectionModel());

        Accordion acc = new Accordion();
        fillWith(acc.getPanes(), i -> new TitledPane("Accordion" + i, placeholder()));

        listener(acc);
        acc.expandedPaneProperty()
            .addListener(_ -> System.out.println("Accordion changed: " + acc.getExpandedPane()));

        Pagination pagination = new Pagination();
        pagination.setPageFactory(_ -> placeholder());
        pagination.setPageCount(10);
        listener(pagination);
        pagination.currentPageIndexProperty()
            .addListener(_ -> System.out.println("Pagination changed: " + pagination.getCurrentPageIndex()));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMinHeight(256);
        scrollPane.setContent(placeholderSize(1024));
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToHeight(true);
        listener(scrollPane);

        content.getChildren().addAll(splitPane, tp, tabPane, acc, pagination, scrollPane);

        return content;
    }

    private static <S> void fillWith(ObservableList<S> list, Function<Integer, S> factory) {
        for (int i = 0; i < 10; i++) {
            list.add(factory.apply(i));
        }
    }

    //
    public static class Person {

        private long id;
        private String firstName;
        private String lastName;
        private String email;
        private int age;
        private int height;
        private int weightKg;
        private int numberOfChildren;
        private double salary;
        private boolean isActive;
        private boolean isVerified;
        private String jobTitle;
        private String company;
        private String department;
        private String employmentStatus;
        private String educationLevel;
        private String maritalStatus;
        private String nationality;
        private String bloodType;
        private String phoneNumber;

        public int getAge() {
            return age;
        }

        public String getBloodType() {
            return bloodType;
        }

        public String getCompany() {
            return company;
        }

        public String getDepartment() {
            return department;
        }

        public String getEducationLevel() {
            return educationLevel;
        }

        public String getEmail() {
            return email;
        }

        public String getEmploymentStatus() {
            return employmentStatus;
        }

        public String getFirstName() {
            return firstName;
        }

        public int getHeight() {
            return height;
        }

        public long getId() {
            return id;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public String getLastName() {
            return lastName;
        }

        public String getMaritalStatus() {
            return maritalStatus;
        }

        public String getNationality() {
            return nationality;
        }

        public int getNumberOfChildren() {
            return numberOfChildren;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public double getSalary() {
            return salary;
        }

        public int getWeightKg() {
            return weightKg;
        }

        public boolean isActive() {
            return isActive;
        }

        public boolean isVerified() {
            return isVerified;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setBloodType(String bloodType) {
            this.bloodType = bloodType;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public void setEducationLevel(String educationLevel) {
            this.educationLevel = educationLevel;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setEmploymentStatus(String employmentStatus) {
            this.employmentStatus = employmentStatus;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setMaritalStatus(String maritalStatus) {
            this.maritalStatus = maritalStatus;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public void setNumberOfChildren(int numberOfChildren) {
            this.numberOfChildren = numberOfChildren;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setSalary(double salary) {
            this.salary = salary;
        }

        public void setVerified(boolean verified) {
            isVerified = verified;
        }

        public void setWeightKg(int weightKg) {
            this.weightKg = weightKg;
        }
    }
}