<template>
    <v-container>
        <v-switch v-model="isMarkdown" label="use markdown" :disabled="disable"></v-switch>
        <TipTap v-model="content" v-if="!isMarkdown" :placeholder="placeholder" />
        <MarkdownEditor v-if="isMarkdown" v-model="content" :placeholder="placeholder" />
    </v-container>
</template>

<script>
import TipTap from './TipTap.vue'
import MarkdownEditor from './MarkdownEditor.vue'

export default {
    name: 'Editor',
    data() {
        return {
            isMarkdown: false,
            content: this.value,
            disable: false
        }
    },
    components: {
        TipTap,
        MarkdownEditor
    },
    watch: {
        content(val) {
            this.$emit('input', val);
        },
        value(val) {
            console.log(val)
            this.content = val;
        }
    },
    props: {
        placeholder: String,
        value: String
    }
}
</script>