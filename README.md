# ImageFeedView

[![Build Status](https://travis-ci.org/oliveroneill/imagefeedview.svg?branch=master)](https://travis-ci.org/oliveroneill/imagefeedview)

A simple Android library for creating data feeds so that data can be loaded
dynamically as the user scrolls. This is inspired by scrolling through photos
on Facebook or Instagram.

This is still a work in progress. See [TODO](#todo)

This library uses [GestureViews](https://github.com/alexvasilkov/GestureViews)
for its photo viewer and wraps this for easy usage with a feed.

This is roughly based on the `FeedCollectionViewController` library for Swift
that you can find [here](https://github.com/oliveroneill/FeedCollectionViewController)

## Example

To run the example project, clone the repo, and run the `app` configuration.
The example project demonstrates the functionality without using any actual content, it creates
coloured images to illustrate its use with a large amount of content.

## Installation
Through `build.gradle`, coming soon.

## Usage

To set up a feed, you need to extend `ImageFeedView` with your image class:
```kotlin
class ExampleImageFeedView : ImageFeedView<ExampleImageClass> {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
}
```
Then use this class in your layout file:
```xml
<com.oliveroneill.imagefeedview.ImageFeedView
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
Then implement an `ImageFeedController` for loading images:
```kotlin
    override fun loadImage(item: ExampleImageClass, imgView:ImageView, listener: LoadListener?) {
        // TODO
    }

    override fun recycleImage(imgView:ImageView) {
        // TODO
    }

    override fun clear() {
        // TODO
    }
```
Then set up an `ImageFeedConfig`:
```kotlin
val config = ImageFeedConfig(controller)
        .setToolbar(toolbar)
        .setTranslucentStatusBar(true)
feed.show(config)
```
For custom grid items or photo viewers, use
`setGridAdapter(adapter : PhotoListAdapter<T>)` and
`setPagerAdapter(adapter : PhotoPagerAdapter<T>)`.

## Testing
Testing is done through JUnit, these tests are located in the `imagefeedview`.

## Todo
- Figure out why double tap breaks with data uri images when loaded a second time
- Set placeholder images
- Make pager toolbar easily configurable
- Make grid item views configurable
- Make pager item views configurable (specify a common layout and have simple setters for that)
- Allow custom error messages

## Author

Oliver O'Neill

## License

imagefeedview is available under the MIT license. See the LICENSE file for more info.