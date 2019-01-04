# Alexa plugin for DialogOS

This is a plugin for [DialogOS](https://www.dialogos.app/) that allows you to run a DialogOS dialog as a skill for [Amazon Alexa](https://en.wikipedia.org/wiki/Amazon_Alexa). The dialog can be developed and tested within DialogOS, potentially without any programming. It can then be deployed as an Alexa skill.

The plugin defines two new node types: Alexa input nodes and Alexa output nodes. Alexa input nodes send a prompt to Alexa, which is then spoken by the user's Echo or other device. After the user responds, the input node continues execution. Alexa output nodes can be used to send a message to the user.

## Intents

Alexa intents are represented as DialogOS [structs](https://github.com/dialogos-project/dialogos/wiki/Expressions). The struct for an intent contains the intent name under the key `INTENT`; it also has an entry for each slot with a non-null value, with the slot name as key and the slot value as value. Here is an example of a struct that encodes an intent; observe that `pizza` is the literal word the user said for the `food` slot, converted to lowercase.

```
{INTENT="FoodOrderIntent", food="pizza"}
```

In an Alexa input node, you can pattern-match against the intent that was sent to you by Alexa using arbitrary [patterns](https://github.com/dialogos-project/dialogos/wiki/Patterns). For instance, you can check whether the intent is a `FoodOrderIntent` with the following pattern:

```
{INTENT="FoodOrderIntent"}
```

You can check whether the `FoodOrderIntent` is for a pizza and then store the quantity in a variable `var`.

```
{INTENT="FoodOrderIntent", food="pizza", quantity=var}
```

Intents are processed from top to bottom. For robustness, you might want to add a catch-all pattern `_` at the bottom of each input node.


## Deploying to Alexa

You can generate a Jar file that is suitable for deployment as an Alexa skill by going to Dialog -> Amazon Alexa plugin and clicking on the "Generate" button next to "Save dialog as Alexa skill". After you choose a filename under which the Jar file will be saved, DialogOS will bundle all the necessary files together.

You will then have to upload your skill to Alexa. Proceed as explained in [Amazon's documentation](https://alexa-skills-kit-sdk-for-java.readthedocs.io/en/latest/Developing-Your-First-Skill.html). You can skip most of the document - DialogOS took care of all the programming details for you -, and simply follow the instructions under [Uploading your skill to AWS Lambda](https://alexa-skills-kit-sdk-for-java.readthedocs.io/en/latest/Developing-Your-First-Skill.html#uploading-your-skill-to-aws-lambda) and [Configuring and testing your skill](https://alexa-skills-kit-sdk-for-java.readthedocs.io/en/latest/Developing-Your-First-Skill.html#configuring-and-testing-your-skill). Note in particular that you need to define intents for your Alexa skill to match the intents your dialog expects from Alexa.

To test your skill, open it in your Alexa Developer Console and go to the "Test" tab. You can then either speak or type utterances.


## Offline testing

The global plugin settings for the Alexa plugin have a checkbox labeled "Testing". By ticking this checkbox, Alexa input and output nodes will not attempt to communicate with Alexa. Instead, when you execute an input node, a window will be displayed in which you can type the intent by hand.

Note: During testing, the NLU capabilities of Alexa are unavailable. Therefore you cannot type the  _utterance_; you must type the _intent_. So for example, you cannot type "I want a pizza" (= a natural-language sentence in English); instead, you need to type

```
{INTENT="FoodOrderIntent", food="pizza"}
```

If the intent has no slots, you can also simply type the intent name (`FoodOrderIntent`); this will then be automatically expanded into a struct as above.

## Known limitations

* Alexa output nodes are implemented in terms of [progressive responses](https://developer.amazon.com/de/docs/custom-skills/send-the-user-a-progressive-response.html). Due to a limitation in the Alexa API, only five (?) progressive responses can be sent for the same request. This means that you can execute at most five Alexa output nodes before you execute an Alexa input node again.
* Dialogs that use Groovy variables are generally supported; in particular, you can use any primitive or builtin type and your own classes. However, Groovy variables that contain values of generic types may not be deserialized correctly. For instance, a `List<Integer>` (say `[1,2,3]`) will be converted into a `List<Double>` (in the example, `[1.0, 2.0, 3.0]`). This is because the variables are serialized into Json, and Json does not distinguish between int and double types. If you need such variables in an Alexa dialog, have a look at how to deal with [generic types](https://github.com/google/gson/blob/master/UserGuide.md#collections-examples) in Gson.

