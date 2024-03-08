# Controls Class Hierarchy

Control classes and their corresponding behaviors, based on jfx22.

(a) denotes an abstract class.

|Control                                     |Default Skin            |Behavior                  |Stateful Behavior|
|:-------------------------------------------|:-----------------------|:-------------------------|:----------------|
|`Accordion`                                 |AccordionSkin           |AccordionBehavior         |yes
|`ButtonBar`                                 |ButtonBarSkin           |-                         |-
|`ChoiceBox`                                 |ChoiceBoxSkin           |ChoiceBoxBehavior         |no
|`ComboBoxBase (a)`                          |-                       |                          |
|`├─ ColorPicker `                           |ColorPickerSkin         |ColorPickerBehavior       |yes
|`├─ ComboBox`                               |ComboBoxListViewSkin    |ComboBoxListViewBehavior  |yes
|`└─ DatePicker`                             |DatePickerSkin          |DatePickerBehavior        |yes
|`HTMLEditor`                                |HTMLEditorSkin          |HTMLEditorBehavior        |no
|`Labeled (a)`                               |-                       |-                         |-
|`├─ ButtonBase (a)`                         |-                       |-                         |-
|`│   ├─ Button`                             |ButtonSkin              |ButtonBehavior            |yes
|`│   ├─ CheckBox`                           |CheckBoxSkin            |ButtonBehavior            |yes
|`│   ├─ Hyperlink`                          |HyperlinkSkin           |ButtonBehavior            |yes
|`│   ├─ MenuButton`                         |MenuButtonSkin          |MenuButtonBehavior        |yes
|`│   │   └─ SplitMenuButton`                |SplitMenuButtonSkin     |SplitMenuButtonBehavior   |yes
|`│   └─ ToggleButton`                       |ToggleButtonSkin        |ToggleButtonBehavior      |yes
|`│       └─ RadioButton`                    |RadioButtonSkin         |ToggleButtonBehavior      |yes
|`├─ Cell`                                   |-                       |-                         |-
|`│   ├─ DateCell`                           |DateCellSkin            |DateCellBehavior          |no
|`│   └─ IndexedCell`                        |-                       |-                         |-
|`│       ├─ ListCell`                       |ListCellSkin            |ListCellBehavior          |yes
|`│       │   ├─ CheckBoxListCell`           |ListCellSkin            |ListCellBehavior          |yes
|`│       │   ├─ ChoiceBoxListCell`          |ListCellSkin            |ListCellBehavior          |yes
|`│       │   ├─ ComboBoxListCell`           |ListCellSkin            |ListCellBehavior          |yes
|`│       │   └─ TextFieldListCell`          |ListCellSkin            |ListCellBehavior          |yes
|`│       ├─ TableCell`                      |TableCellSkin           |TableCellBehavior         |yes
|`│       │   ├─ CheckBoxTableCell`          |TableCellSkin           |TableCellBehavior         |yes
|`│       │   ├─ ChoiceBoxTableCell`         |TableCellSkin           |TableCellBehavior         |yes
|`│       │   ├─ ComboBoxTableCell`          |TableCellSkin           |TableCellBehavior         |yes
|`│       │   ├─ ProgressBarTableCell`       |TableCellSkin           |TableCellBehavior         |yes
|`│       │   └─ TextFieldTableCell`         |TableCellSkin           |TableCellBehavior         |yes
|`│       ├─ TableRow`                       |TableRowSkin            |TableRowBehavior          |yes
|`│       ├─ TreeCell`                       |TreeCellSkin            |TreeCellBehavior          |yes
|`│       ├─ TreeTableCell`                  |TreeTableCellSkin       |TreeTableCellBehavior     |yes
|`│       │   ├─ CheckBoxTreeTableCell`      |TreeTableCellSkin       |TreeTableCellBehavior     |yes
|`│       │   ├─ ChoiceBoxTreeTableCell`     |TreeTableCellSkin       |TreeTableCellBehavior     |yes
|`│       │   ├─ ComboBoxTreeTableCell`      |TreeTableCellSkin       |TreeTableCellBehavior     |yes
|`│       │   ├─ ProgressBarTreeTableCell`   |TreeTableCellSkin       |TreeTableCellBehavior     |yes
|`│       │   └─ TextFieldTreeTableCell`     |TreeTableCellSkin       |TreeTableCellBehavior     |yes
|`│       └─ TreeTableRow`                   |TreeTableRowSkin        |TreeTableRowBehavior      |yes
|`├─ Label`                                  |LabelSkin               |-                         |-
|`└─ TitlePane`                              |TitledPaneSkin          |TitledPaneBehavior        |no
|`ListView`                                  |ListViewSkin            |ListViewBehavior          |yes
|`MenuBar`                                   |MenuBarSkin             |-                         |-
|`Pagination`                                |PaginationSkin          |PaginationBehavior        |no
|`ProgressIndicator`                         |ProgressIndicatorSkin   |-                         |-
|`└─ ProgressBar`                            |ProgressBarSkin         |-                         |-
|`ScrollBar`                                 |ScrollBarSkin           |ScrollBarBehavior         |yes
|`ScrollPane`                                |ScrollPaneSkin          |ScrollPaneBehavior        |no
|`Separator`                                 |SeparatorSkin           |-                         |-
|`Slider`                                    |SliderSkin              |SliderBehavior            |no
|`Spinner`                                   |SpinnerSkin             |SpinnerBehavior           |yes
|`SplitPane`                                 |SplitPaneSkin           |-                         |-
|`TableView`                                 |TableViewSkin           |TableViewBehavior         |yes
|`TabPane`                                   |TabPaneSkin             |TabPaneBehavior           |no
|`TextInputControl (a)`                      |-                       |-                         |-
|`├─ TextArea`                               |TextAreaSkin            |TextAreaBehavior          |yes
|`└─ TextField`                              |TextFieldSkin           |TextFieldBehavior         |yes
|`    └─ PasswordField`                      |TextFieldSkin           |TextFieldBehavior         |yes
|`ToolBar`                                   |ToolBarSkin             |ToolBarBehavior           |no
|`TreeTableView`                             |TreeTableViewSkin       |TreeTableViewBehavior     |yes
|`TreeView`                                  |TreeViewSkin            |TreeViewBehavior          |yes

