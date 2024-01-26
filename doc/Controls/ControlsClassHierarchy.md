# Controls Class Hierarchy

Control classes and their corresponding behaviors, based on jfx22.

(a) denotes an abstract class.

```
|Control                                     |Behavior                  |Stateful?    |
|:-------------------------------------------|:-------------------------|:------------|
|` Accordion`                                  |AccordionBehavior         |yes
|` ButtonBar`                                  |-                         |-
|` ChoiceBox`                                  |ChoiceBoxBehavior         |no
|` ComboBoxBase (a)`                           |                          |
|`  ├─ ColorPicker `                           |ColorPickerBehavior       |yes
|`  ├─ ComboBox `                              |ComboBoxListViewBehavior  |yes
|`  └─ DatePicker`                             |DatePickerBehavior        |yes
| HTMLEditor                                 |HTMLEditorBehavior        |no
| Labeled (a)                                |-                         |-
|  ├─ ButtonBase (a)                         |-                         |-
|  │   ├─ Button                             |ButtonBehavior            |yes
|  │   ├─ CheckBox                           |ButtonBehavior            |yes
|  │   ├─ Hyperlink                          |ButtonBehavior            |yes
|  │   ├─ MenuButton                         |MenuButtonBehavior        |yes
|  │   │   └─ SplitMenuButton                |SplitMenuButtonBehavior   |yes
|  │   └─ ToggleButton                       |ToggleButtonBehavior      |yes
|  │       └─ RadioButton                    |ToggleButtonBehavior      |yes
|  ├─ Cell                                   |-                         |-
|  │   ├─ DateCell                           |DateCellBehavior          |no
|  │   └─ IndexedCell                        |-                         |-
|  │       ├─ ListCell                       |ListCellBehavior          |yes
|  │       │   ├─ CheckBoxListCell           |ListCellBehavior          |yes
|  │       │   ├─ ChoiceBoxListCell          |ListCellBehavior          |yes
|  │       │   ├─ ComboBoxListCell           |ListCellBehavior          |yes
|  │       │   └─ TextFieldListCell          |ListCellBehavior          |yes
|  │       ├─ TableCell                      |TableCellBehavior         |yes
|  │       │   ├─ CheckBoxTableCell          |TableCellBehavior         |yes
|  │       │   ├─ ChoiceBoxTableCell         |TableCellBehavior         |yes
|  │       │   ├─ ComboBoxTableCell          |TableCellBehavior         |yes
|  │       │   ├─ ProgressBarTableCell       |TableCellBehavior         |yes
|  │       │   └─ TextFieldTableCell         |TableCellBehavior         |yes
|  │       ├─ TableRow                       |TableRowBehavior          |yes
|  │       ├─ TreeCell                       |TreeCellBehavior          |yes
|  │       ├─ TreeTableCell                  |TreeTableCellBehavior     |yes
|  │       │   ├─ CheckBoxTreeTableCell      |TreeTableCellBehavior     |yes
|  │       │   ├─ ChoiceBoxTreeTableCell     |TreeTableCellBehavior     |yes
|  │       │   ├─ ComboBoxTreeTableCell      |TreeTableCellBehavior     |yes
|  │       │   ├─ ProgressBarTreeTableCell   |TreeTableCellBehavior     |yes
|  │       │   └─ TextFieldTreeTableCell     |TreeTableCellBehavior     |yes
|  │       └─ TreeTableRow                   |TreeTableRowBehavior      |yes
|  ├─ Label                                  |-                         |-
|  └─ TitlePane                              |TitledPaneBehavior        |no
| ListView                                   |ListViewBehavior          |yes
| MenuBar                                    |-                         |-
| Pagination                                 |PaginationBehavior        |no
| ProgressIndicator                          |-                         |-
|  └─ ProgressBar                            |-                         |-
| ScrollBar                                  |ScrollBarBehavior         |yes
| ScrollPane                                 |ScrollPaneBehavior        |no
| Separator                                  |-                         |-
| Slider                                     |SliderBehavior            |no
| Spinner                                    |SpinnerBehavior           |yes
| SplitPane                                  |-                         |-
| TableView                                  |TableViewBehavior         |yes
| TabPane                                    |TabPaneBehavior           |no
| TextInputControl (a)                       |-                         |-
|  ├─ TextArea                               |TextAreaBehavior          |yes
|  └─ TextField                              |TextFieldBehavior         |yes
|      └─ PasswordField                      |TextFieldBehavior         |yes
| ToolBar                                    |ToolBarBehavior           |no
| TreeTableView                              |TreeTableViewBehavior     |yes
| TreeView                                   |TreeViewBehavior          |yes
```

