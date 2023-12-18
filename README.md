## 依赖 ##
1. 添加maven仓
   ```
   dependencyResolutionManagement {
     repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
     repositories {
       mavenCentral()
       maven { url 'https://jitpack.io' }
     }
   }
   ```
3. app层级下的build.gradle添加依赖
   ```
   dependencies {
     implementation 'com.github.fullgas-jy:siwtchView:Tag'
   }
    ```
## 使用 ##
- xml
  ```
  <com.jy.switchview.SwitchView
    android:id="@+id/sw"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:clickable="true" />
  ```
- 可用属性
  ```
  <declare-styleable name="SwitchView">
    <attr format="boolean" name="switch_checked"/>  //开关状态
    <attr format="color" name="switch_on_color"/>  //开关打开时背景颜色
    <attr format="color" name="switch_off_color"/>  //开关关闭时背景颜色
    <attr format="color" name="switch_circle_color"/>  //开关小圆圈颜色
    <attr format="dimension" name="switch_circle_r"/>  //开关小圆圈半径
    <attr format="dimension" name="switch_circle_margin"/>  //开关小圆圈距四周margin值
  </declare-styleable>
  ```
- 开关切换监听
  ```
  mBinding.sw.setICheckChanged(object : SwitchView.ICheckChangedListener {
    override fun onCheckChanged(isChecked: Boolean) {
      log("isChecked:${isChecked}")
    }
  })
  ```
