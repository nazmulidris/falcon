# Project Falcon

- Note the website for this project is at [uxd.engineering/](http://uxd.engineering). 
- This URL points to the HTML and CSS files in the `docs/` folder. 
- Also this URL goes to the same place 
[github.com/nazmulidris/falcon](https://github.com/nazmulidris/falcon).

# A study in RecyclerView

## Video of part of the app in action
<img 
src="https://github.com/nazmulidris/recyclerview/blob/master/astudyinrecyclerview.gif?raw=true" 
width="500"/>

## Overview
This project is an exploration of things that one can do with the RecyclerView. 
A lot of emphasis is placed on animation (physics based, and animator based). An 
[ItemTouchHelper.Callback](https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.Callback.html)
is also provided to demonstrate how to respond to user gesture inputs (drag and drop,
swipe to dismiss) and how to integrate this with animations. Animations are also provided
when a list view is entered (using layout animators).

## Resources for learning
Here are some great resources on learning about RecyclerView.
- [Caster.IO course on RecyclerView](https://caster.io/courses/recycler-view)
- [Adding click listeners](https://antonioleiva.com/recyclerview-listener/)

These great tutorials cover the specifics of how to get drag and drop, and
swipe to dismiss working with RecyclerViews.
- [Drag and Swipe w/ RecyclerView Part 1](https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf)
- [Drag and Swipe w/ RecyclerView Part 2](https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-6a6f0c422efd)

To learn more about Material Motion (ripples, stateListAnimators, etc) here are some great 
resources.
- [Mastering Material Motion](https://www.youtube.com/watch?v=aZ5V5e-phR8)

More info on animations in android (use property animators, not view or tweening animators).
- [Property animation, not View animation](https://developer.android.com/guide/topics/graphics/prop-animation.html)

More info on physics based animation:
- [Physics based animation tutorial](https://code.tutsplus.com/tutorials/adding-physics-based-animations-to-android-apps--cms-29053)
- [Physics based animation tutorial](http://www.thedroidsonroids.com/blog/android/springanimation-examples)
- [Physics based animation sample](https://proandroiddev.com/introduction-to-physics-based-animations-in-android-1be27e468835)
- [API docs on DAC](https://developer.android.com/guide/topics/graphics/physics-based-animation.html)

These resources are great for best practices for naming things in Android 
(which were used to name the multitude of layout assets in this project).
- [Guidelines for naming things in Android](https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md)
- [Cheat sheet for naming things in Android](https://jeroenmols.com/blog/2016/03/07/resourcenaming/)