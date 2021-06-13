<template>
    <div class="editor-box">
        <div>
            <v-btn @click="editor.chain().focus().toggleBold().run()" :class="{ 'is-active': editor && editor.isActive('bold') }" icon :color="getButtonColor('bold')">
                <v-icon>mdi-format-bold</v-icon>
            </v-btn>
            <v-btn @click="editor.chain().focus().toggleItalic().run()" :class="{ 'is-active': editor && editor.isActive('italic') }" icon :color="getButtonColor('italic')">
                <v-icon>mdi-format-italic</v-icon>
            </v-btn>
            <v-btn @click="editor.chain().focus().toggleUnderline().run()" :class="{ 'is-active': editor && editor.isActive('underline') }" icon :color="getButtonColor('underline')">
                <v-icon>mdi-format-underline</v-icon>
            </v-btn>
            <v-btn @click="editor.chain().focus().toggleCode().run()" :class="{ 'is-active': editor && editor.isActive('code') }" icon :color="getButtonColor('code')">
                <v-icon>mdi-code-braces</v-icon>
            </v-btn>
            <v-btn  @click="editor.chain().focus().toggleHighlight({color: '#fff897'}).run()" :class="{ 'is-active': editor && editor.isActive('highlight', {color: '#fff897'})}" icon :color="getButtonColor('highlight')">
                <v-icon>mdi-format-color-highlight</v-icon>
            </v-btn>
            <v-btn @click="editor.chain().focus().toggleHeading({ level: 1 }).run()" :class="{ 'is-active': editor && editor.isActive('heading', { level: 1})}" :color="getButtonColor('heading', { level: 1})" icon>
                <v-icon>mdi-format-header-1</v-icon>
            </v-btn>
            <v-btn @click="editor.chain().focus().toggleHeading({ level: 2 }).run()" :class="{ 'is-active': editor && editor.isActive('heading', { level: 2})}" :color="getButtonColor('heading', { level: 2})" icon>
                <v-icon>mdi-format-header-2</v-icon>
            </v-btn>
            <v-btn @click="editor.chain().focus().toggleHeading({ level: 3 }).run()" :class="{ 'is-active': editor && editor.isActive('heading', { level: 3})}" :color="getButtonColor('heading', { level: 3})" icon>
                <v-icon>mdi-format-header-3</v-icon>
            </v-btn>
        </div>
        <editor-content :editor="editor" class/>
    </div>
</template>

<script>
import { Editor, EditorContent } from '@tiptap/vue-2';
import StarterKit from '@tiptap/starter-kit';
import Placeholder from '@tiptap/extension-placeholder';
import Underline from '@tiptap/extension-underline';
import Highlight from '@tiptap/extension-highlight';
import Heading from '@tiptap/extension-heading';

export default {
    components: {
        EditorContent,
    },
    data() {
        return {
            editor: null,
        }
    },
    props: {
        value: String,
        placeholder: String
    },
    mounted() {
        this.editor = new Editor({
            content: this.value,
            extensions: [
                StarterKit,
                Placeholder.configure({
                    placeholder: this.placeholder
                }),
                Underline,
                Highlight.configure({
                    multicolor: true
                }),
                Heading.configure({
                    levels: [1, 2, 3]
                })
            ],
            onUpdate: () => {
                this.$emit('input', this.editor.getHTML())
            }
        })
    },
    watch: {
        value(value) {
            const isSame = this.editor.getHTML() === value;
            if (isSame) return;
            this.editor.commands.setContent(value, false);
        }
    },
    beforeDestroy() {
        this.editor.destroy();
    },
    methods: {
        getButtonColor(... obj) {
            if (this.editor != null && this.editor.isActive(... obj)) {
                return 'blue';
            } else {
                return 'undefined';
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.editor-box {
    display: block;
    border: 2rem;
    border-color: #fff897;
}
::v-deep {
  /* Basic editor styles */

  .ProseMirror:focus {
      outline: none;
  }

  .ProseMirror {

      padding: 1rem;
      color: black;
      font-size: 1rem;
      min-height: 10rem;
      max-height: 20rem;
      overflow-y: auto;

    h1,h2,h3 {
        line-height: 2rem;
    }
  }

  /* Placeholder (at the top) */
  .ProseMirror p.is-editor-empty:first-child::before {
    content: attr(data-placeholder);
    float: left;
    color: #ced4da;
    pointer-events: none;
    height: 0;
  }

  

  button {
    color: black;
  }
}
</style>