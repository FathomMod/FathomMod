// Made with Blockbench 4.10.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelWood_Bat_Projectile<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "wood_bat_projectile"), "main");
	private final ModelPart bb_main;

	public ModelWood_Bat_Projectile(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
				CubeListBuilder.create().texOffs(8, 26)
						.addBox(-1.0F, -31.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(22, 18)
						.addBox(-1.2F, -31.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(42, 9)
						.addBox(-1.3F, -23.0F, -1.1F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(42, 0)
						.addBox(-1.4F, -22.2F, -1.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 42)
						.addBox(-0.8F, -22.2F, -1.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(37, 40)
						.addBox(-0.8F, -22.2F, -0.6F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(14, 41)
						.addBox(-1.4F, -22.2F, -0.6F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(14, 34)
						.addBox(-0.7F, -21.7F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(29, 40)
						.addBox(-1.65F, -21.45F, 0.75F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(40, 13)
						.addBox(-1.65F, -21.45F, -1.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(8, 40)
						.addBox(-1.55F, -21.45F, -1.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(21, 39)
						.addBox(-1.55F, -21.45F, 0.75F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(27, 25)
						.addBox(-1.65F, -21.45F, -1.4F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(24, 0)
						.addBox(0.45F, -21.45F, -1.4F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(34, 15)
						.addBox(-1.5F, -21.7F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 36)
						.addBox(-1.5F, -21.7F, -1.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 1)
						.addBox(-0.7F, -21.7F, -1.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(32, 25)
						.addBox(-1.5F, -20.95F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 33)
						.addBox(-0.7F, -20.95F, -1.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(22, 33)
						.addBox(-0.7F, -20.95F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(30, 33)
						.addBox(-1.5F, -20.95F, -1.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(42, 6)
						.addBox(-0.9F, -23.0F, -1.1F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(42, 3)
						.addBox(-0.9F, -23.0F, -0.7F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 4)
						.addBox(-1.3F, -23.0F, -0.7F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(34, 37)
						.addBox(-1.3F, -31.0F, -1.1F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(38, 24)
						.addBox(-0.9F, -31.0F, -1.1F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(16, 31)
						.addBox(-1.3F, -31.0F, -0.7F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 39)
						.addBox(-0.9F, -31.0F, -0.7F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 31)
						.addBox(-1.4F, -32.0F, -1.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 34)
						.addBox(-0.8F, -32.0F, -1.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(30, 30)
						.addBox(-1.4F, -32.0F, -0.6F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(14, 37)
						.addBox(-0.8F, -32.0F, -0.6F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(20, 36)
						.addBox(-0.7F, -33.0F, -1.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 21)
						.addBox(-0.7F, -33.0F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(28, 36)
						.addBox(-1.5F, -33.0F, -0.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 28)
						.addBox(-1.5F, -33.0F, -1.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 10)
						.addBox(-0.6F, -34.0F, -1.4F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 18)
						.addBox(-0.6F, -34.0F, -0.4F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(22, 30)
						.addBox(-1.6F, -34.0F, -0.4F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(30, 22)
						.addBox(-1.6F, -34.0F, -1.4F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(30, 12)
						.addBox(-0.5F, -35.0F, -1.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(30, 19)
						.addBox(-0.5F, -35.0F, -0.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(28, 16)
						.addBox(-1.7F, -35.0F, -0.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(29, 0)
						.addBox(-1.7F, -35.0F, -1.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(24, 5)
						.addBox(-1.7F, -48.25F, -1.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(16, 28)
						.addBox(-1.7F, -48.25F, -0.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(30, 6)
						.addBox(-0.5F, -48.25F, -1.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(30, 9)
						.addBox(-0.5F, -48.25F, -0.3F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 30)
						.addBox(-1.6F, -48.35F, -1.4F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(30, 3)
						.addBox(-1.6F, -48.35F, -0.4F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(36, 7)
						.addBox(-0.6F, -48.35F, -1.4F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(8, 36)
						.addBox(-0.6F, -48.35F, -0.4F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
						.addBox(-0.4F, -48.0F, -0.2F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(8, 0)
						.addBox(-0.399F, -48.0F, -1.601F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(14, 13)
						.addBox(-1.8F, -48.0F, -1.6F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 15)
						.addBox(-1.801F, -48.0F, -0.199F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(22, 8)
						.addBox(-1.2F, -31.0F, -0.8F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(16, 0)
						.addBox(-1.0F, -31.0F, -0.8F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}